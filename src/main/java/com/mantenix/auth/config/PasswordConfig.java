package com.mantenix.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Password hashing with bcrypt. Security policy: cost factor 12 or more. */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${app.auth.bcrypt-strength:12}") int strength) {
        if (strength < 12) {
            throw new IllegalStateException("bcrypt strength must be 12 or more (security policy), got " + strength);
        }
        return new BCryptPasswordEncoder(strength);
    }
}
