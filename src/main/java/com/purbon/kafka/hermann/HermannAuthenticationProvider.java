package com.purbon.kafka.hermann;

import com.purbon.kafka.hermann.managers.RegistrationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class HermannAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (shouldAuthenticateAgainstThirdPartySystem()) {
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
        }
        return null;
    }

    private boolean shouldAuthenticateAgainstThirdPartySystem() {
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    }

    public void setRegistrationManager(RegistrationService registrationService) {
    }
}
