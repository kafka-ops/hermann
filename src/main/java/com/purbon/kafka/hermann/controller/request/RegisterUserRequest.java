package com.purbon.kafka.hermann.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RegisterUserRequest {

    private String username;
    private String password;
    private List<String> roles = new ArrayList<>();

    public void addRole(String role) {
        this.roles.add(role);
    }
}
