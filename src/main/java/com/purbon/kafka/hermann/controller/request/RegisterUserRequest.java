package com.purbon.kafka.hermann.controller.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterUserRequest {

    private String username;
    private String password;
}
