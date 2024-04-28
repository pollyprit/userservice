package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dtos.SendEmailEventDto;
import com.userservice.dtos.UserDto;
import com.userservice.models.Token;
import com.userservice.models.User;
import com.userservice.repositories.TokenRepository;
import com.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UserService {
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private JWTService jwtService;
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    @Value("${enable.email.service}")
    private boolean enableEmailService;

    public UserService(UserRepository userRepository, TokenRepository tokenRepository,
                       JWTService jwtService, BCryptPasswordEncoder bcryptPasswordEncoder,
                       KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public User signup(String name, String password, String email, List<String> roles) {
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bcryptPasswordEncoder.encode(password));
        user.setEmailVerified(true);   // TODO: should be 'false' and later set to 'true' on email verification.

        User savedUser = userRepository.save(user);

        if (enableEmailService) {
            // Send an async welcome email through kafka + email service
            SendEmailEventDto sendEmailEventDto = new SendEmailEventDto();
            sendEmailEventDto.setTo(email);
            sendEmailEventDto.setFrom("virdi.pritpal@gmail.com");
            sendEmailEventDto.setSubject("Welcome to ECom! Your heaven for shopping!!");
            sendEmailEventDto.setBody("Hello " + name + ", \n Thanks for signing up for ECom services."
                    + "\n We wish to serve you the best!!");

            try {
                kafkaTemplate.send("sendEmail", objectMapper.writeValueAsString(sendEmailEventDto));
            } catch (JsonProcessingException e) {
                System.out.println("Exception while sending welcome email to user: " + email);
                e.printStackTrace();
            }
        }

        return savedUser;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> allUsers = new ArrayList<>();

        for (User user: users)
            allUsers.add(UserDto.from(user));

        return allUsers;
    }

    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // TODO: throw user email not found exception
            return null;
        }

        User user = userOptional.get();

        if (!bcryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            // TODO: throw invalid credentials exception
            return null;
        }

        // Token token = generateToken(user, RandomStringUtils.randomAlphanumeric(128));
        String jwtToken = jwtService.createJWT(user);
        Token token = generateToken(user, jwtToken);

        return tokenRepository.save(token);
    }

    public void logout(String tokenValue) {
        Optional<Token> tokenOptional
                = tokenRepository.findByValueAndDeletedEquals(tokenValue, false);

        if (tokenOptional.isEmpty())
            return; // nothing to do or throw exception.

        Token token = tokenOptional.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }

    private Token generateToken(User user, String value) {
        LocalDateTime limit = LocalDateTime.now().plusMinutes(30);
        Date expiryAt = Date.from(ZonedDateTime.of(limit, ZoneId.systemDefault()).toInstant());

        Token token = new Token();

        token.setUser(user);
        token.setExpiryAt(expiryAt);
        token.setValue(value);

        return token;
    }

    public UserDto validateJWTToken(String tokenValue) {
        Date now = new Date();
        Optional<Token> optionalToken = tokenRepository.
                    findByValueAndDeletedEquals(tokenValue, false);

        if (optionalToken.isEmpty())
            return null;

        Token token = optionalToken.get();
        String value = token.getValue();

        String userEmail = jwtService.decodeJWT(value);

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty())
            return null;

        return UserDto.from(optionalUser.get());
    }
}
