package com.userservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "role")
public class Role extends BaseModel {
    private String rolename;
}
