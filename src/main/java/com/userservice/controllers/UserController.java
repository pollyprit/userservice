package com.userservice.controllers;

import com.userservice.dtos.LoginRequestDto;
import com.userservice.dtos.LogoutRequestDto;
import com.userservice.dtos.SignupRequestDto;
import com.userservice.dtos.UserDto;
import com.userservice.models.Token;
import com.userservice.services.UserService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  User Service APIs supported:
 *  1. Get all Users (GET /users)
 *  2. Sign-up a new User (POST /users/signup)
 *  3. Login a User (POST /users/login)
 *  4. Logout a User (POST /users/logout)
 *  5. Validate a Token (POST /users/validate/{token}
 */
@Getter
@Setter
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupRequestDto signupRequestDto) {
        String user = signupRequestDto.getName();
        String password = signupRequestDto.getPassword();
        String email = signupRequestDto.getEmail();
        List<String> roles = signupRequestDto.getRoles();

        return UserDto.from(userService.signup(user, password, email, roles));
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        userService.logout(logoutRequestDto.getToken());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // For OAuth2.0 from product-service
    @PostMapping("/validate/{token}")
    public ResponseEntity<UserDto> validateJWTToken(@PathVariable(name = "token") @NonNull String token) {
        return new ResponseEntity<UserDto>(userService.validateJWTToken(token), HttpStatus.OK);
    }
}
