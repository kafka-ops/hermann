package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.controller.exceptions.UserRegistrationException;
import com.purbon.kafka.hermann.controller.request.RegisterUserRequest;
import com.purbon.kafka.hermann.model.Role;
import com.purbon.kafka.hermann.model.User;
import com.purbon.kafka.hermann.storage.RoleRepository;
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

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User register(RegisterUserRequest request) {

        if (userRepository.existsById(request.getUsername())) {
           throw new UserRegistrationException("Can't register this user");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        request.getRoles().forEach(roleName -> {
            Role userRole = findRoleByName(roleName);
            user.addRole(userRole);
        });

        userRepository.save(user);
        return user;
    }

    public List<User> all() {
        var all = new ArrayList<User>();
        userRepository.findAll().forEach(all::add);
        return all;
    }

    public void addRole(Role role) {
        roleRepository.save(role);
    }

    public Role findRoleByName(String roleName) {
        return roleRepository.findById(roleName).orElse(new Role("default"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findById(username);
        return optionalUser
                .orElseThrow(() -> new UsernameNotFoundException("User: "+username+" not found"));
    }
}
