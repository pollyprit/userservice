package com.userservice.dtos;

import com.userservice.models.Role;
import com.userservice.models.User;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    private boolean isEmailVerified;

    @ManyToMany
    private List<RoleDto> roles;

    public static UserDto from(User user) {
        if (user == null)
            return null;

        UserDto userDto = new UserDto();

        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setEmailVerified(user.isEmailVerified());

        List<RoleDto> userRoles = new ArrayList<>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                RoleDto roleDto = new RoleDto();
                roleDto.setRolename(role.getRolename());
                userRoles.add(roleDto);
            }
        }

        userDto.setRoles(userRoles);

        return userDto;
    }
}
