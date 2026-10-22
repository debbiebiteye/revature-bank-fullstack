package com.revature.bank.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.context.*;
import org.springframework.security.web.csrf.*;

@Configuration
public class SessionConfig {

    //These beans have separate jobs:
    //- SecurityContextRepository saves the authenticated user’s security context in their session.
    //- CsrfTokenRepository stores the expected CSRF token in that session.
    //- SessionAuthenticationStrategy changes the session ID and clears the old CSRF token after
    // successful login.
    //Changing the session ID helps prevent session fixation—reusing a session identifier established
    // before login.

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
            CsrfTokenRepository csrfTokenRepository) {

        return new CompositeSessionAuthenticationStrategy(
                List.of(
                        new ChangeSessionIdAuthenticationStrategy(),
                        new CsrfAuthenticationStrategy(csrfTokenRepository)
                )
        );
    }
}