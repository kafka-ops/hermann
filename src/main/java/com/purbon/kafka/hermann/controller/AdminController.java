package com.purbon.kafka.hermann.controller;

import com.purbon.kafka.hermann.controller.request.RegisterUserRequest;
import com.purbon.kafka.hermann.controller.response.UserResponse;
import com.purbon.kafka.hermann.managers.RegistrationManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/admin/users")
public class AdminController {

    RegistrationManager register;

    public AdminController(RegistrationManager register) {
        this.register = register;
    }

    @PutMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public boolean register(RegisterUserRequest request) {
        register.register(request);
        return true;
    }

    @GetMapping
    public List<UserResponse> all() {
        return register.all().stream().map(u -> new UserResponse(u.getUsername())).collect(Collectors.toList());
    }
}
