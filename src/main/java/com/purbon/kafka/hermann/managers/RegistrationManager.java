package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.controller.exceptions.UserRegistrationException;
import com.purbon.kafka.hermann.controller.request.RegisterUserRequest;
import com.purbon.kafka.hermann.model.User;
import com.purbon.kafka.hermann.storage.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationManager {

    public UserRepository repository;

    public RegistrationManager(UserRepository repository) {
        this.repository = repository;
    }

    public User register(RegisterUserRequest request) {

       // if (repository.existsById(request.getUsername())) {
       //    throw new UserRegistrationException("Can't register this user");
       // }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(hashhash(request.getPassword()));
        user.setAdmin(repository.count() == 0);

        repository.save(user);

        return user;
    }

    public List<User> all() {
        var all = new ArrayList<User>();
        repository.findAll().forEach(all::add);
        return all;
    }

    private String hashhash(String password) {
       return password;
    }
}
