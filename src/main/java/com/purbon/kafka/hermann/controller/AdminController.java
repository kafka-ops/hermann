package com.purbon.kafka.hermann.controller;

import com.purbon.kafka.hermann.controller.request.RegisterUserRequest;
import com.purbon.kafka.hermann.controller.response.UserResponse;
import com.purbon.kafka.hermann.managers.RegistrationService;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/admin/users")
public class AdminController {

    RegistrationService register;
    PasswordEncoder passwordEncoder;

    public AdminController(RegistrationService register, PasswordEncoder passwordEncoder) {
        this.register = register;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse create(@RequestBody RegisterUserRequest request) {
        request.setPassword(hashhash(request.getPassword()));
        register.register(request);
        return new UserResponse(request.getUsername());
    }

    @GetMapping
    public List<UserResponse> all() {
        return register.all().stream().map(u -> new UserResponse(u.getUsername())).collect(Collectors.toList());
    }

    private String hashhash(String password) {
        return passwordEncoder.encode(password);
    }
}
