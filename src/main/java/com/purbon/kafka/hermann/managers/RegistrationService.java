package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.controller.exceptions.UserRegistrationException;
import com.purbon.kafka.hermann.controller.request.RegisterUserRequest;
import com.purbon.kafka.hermann.model.User;
import com.purbon.kafka.hermann.storage.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService implements UserDetailsService {

    private UserRepository repository;

    public RegistrationService(UserRepository repository) {
        this.repository = repository;
    }

    public User register(RegisterUserRequest request) {

        if (repository.existsById(request.getUsername())) {
           throw new UserRegistrationException("Can't register this user");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setAdmin(repository.count() < 1);

        repository.save(user);
        return user;
    }

    public List<User> all() {
        var all = new ArrayList<User>();
        repository.findAll().forEach(all::add);
        return all;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = repository.findById(username);
        return optionalUser
                .orElseThrow(() -> new UsernameNotFoundException("User: "+username+" not found"));
    }
}
