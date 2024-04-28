package com.userservice.security.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.userservice.models.Role;
import org.springframework.security.core.GrantedAuthority;

@JsonDeserialize
public class CustomGrantedAuthority implements GrantedAuthority {
    private String authority;

    public CustomGrantedAuthority() {}

    public CustomGrantedAuthority(Role role) {
        this.authority = role.getRolename();
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
