package com.purbon.kafka.hermann.controller.request;

import lombok.Data;

@Data
public class RegisterUserRequest {

    private String username;
    private String password;
}
