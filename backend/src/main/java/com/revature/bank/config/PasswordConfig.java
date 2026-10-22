package com.revature.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

// Identifies a class that defines Spring-managed objects.
@Configuration
public class PasswordConfig {


    // registers the returned encoder so Spring can inject it into other classes.
    // The arguments configure an empty additional secret, a 16-byte random salt,
    // 600,000 iterations, and PBKDF2 with HMAC-SHA256
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(
                "",
                16,
                600_000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm
                        .PBKDF2WithHmacSHA256
        );
    }
}