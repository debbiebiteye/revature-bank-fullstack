package com.revature.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CsrfTokenRepository csrfTokenRepository) throws Exception  {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/hello", "/api/hello/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/users", "/api/auth/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf")
                        .permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .securityContext(context -> context
                .securityContextRepository(securityContextRepository)
                .requireExplicitSave(true)
        )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) ->
                                response.setStatus(401)
                        )
                )

                //This configuration:
                //- Uses POST /api/auth/logout.
                //- Invalidates the server-side session.
                //- Clears authentication and deletes the session cookie.
                //- Returns 204 No Content on success—an empty response body is expected.
                //Keep CSRF protection enabled. Logout is a POST, so it needs a valid token.
        .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) ->
                        response.setStatus(204)
                )
                .permitAll()
        );

        return http.build();
    }
}


// The settings mean:
//- permitAll() allows a route without login.
//- authenticated() requires authentication for other routes.
//- csrf(...) keeps CSRF protection enabled.
//- formLogin / httpBasic disabled: we’ll use our JSON login endpoint.
//- authenticationEntryPoint returns 401 when authentication is required.
//Public routes still require a CSRF token for POST requests. Authentication and CSRF
// protection are separate checks. Spring stores the expected token in the session by default.