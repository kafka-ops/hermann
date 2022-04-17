package com.purbon.kafka.hermann;

import com.purbon.kafka.hermann.controller.request.RegisterUserRequest;
import com.purbon.kafka.hermann.managers.RegistrationService;
import com.purbon.kafka.hermann.model.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;


@Component
public class ApplicationBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationBootstrap.class);

    private RegistrationService registrationService;
    private PasswordEncoder encoder;

    public ApplicationBootstrap(RegistrationService registrationService, PasswordEncoder encoder) {
        this.registrationService = registrationService;
        this.encoder = encoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bootstrapRoles();
        createAdmin();
    }

    private void createAdmin() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("admin");
        request.setPassword(encoder.encode("pass"));
        request.addRole("admin");

        try {
            registrationService.register(request);
        } catch (Exception ex) {
            LOGGER.debug(ex.getMessage());
        }
    }

    private void bootstrapRoles() {
        asList("admin", "user")
                .forEach( roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    registrationService.addRole(role);
                });
    }
}
