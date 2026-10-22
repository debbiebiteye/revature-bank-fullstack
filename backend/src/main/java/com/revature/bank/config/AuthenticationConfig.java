package com.revature.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.revature.bank.security.BankUserDetailsService;

@Configuration
public class AuthenticationConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            BankUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}

//There are two responsibilities here:
//- DaoAuthenticationProvider loads the user through BankUserDetailsService and checks
// the password through PasswordEncoder. Spring documentation
//- ProviderManager implements AuthenticationManager and delegates authentication to that provider.
//Spring supplies both method parameters because they’re already registered beans.

//The authentication flow will be:
//Entered credentials
//    → AuthenticationManager
//    → DaoAuthenticationProvider
//    → Load user + check password hash
//    → Authenticated result or exception
