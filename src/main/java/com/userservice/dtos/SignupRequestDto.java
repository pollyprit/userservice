package com.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignupRequestDto {
    private String name;
    private String password;
    private String email;
    private List<String> roles;
}
