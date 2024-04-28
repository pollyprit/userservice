package com.userservice.models;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "user")
public class User extends BaseModel {
    private String name;
    private String hashedPassword;
    private String email;
    private boolean isEmailVerified;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}
