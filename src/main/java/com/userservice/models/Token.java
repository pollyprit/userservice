package com.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "token")
public class Token extends BaseModel {
    private String value; // JWT or base64 token
    private Date expiryAt;
    @ManyToOne
    private User user;
}
