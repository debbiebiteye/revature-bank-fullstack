package com.revature.bank.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import com.revature.bank.dto.LoginRequest;
import com.revature.bank.dto.UserResponse;
import com.revature.bank.entity.BankUser;
import com.revature.bank.exception.InvalidCredentialsException;
import com.revature.bank.repository.BankUserRepository;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionStrategy;
    private final SecurityContextRepository contextRepository;
    private final BankUserRepository userRepository;

    public LoginController(
            AuthenticationManager authenticationManager,
            SessionAuthenticationStrategy sessionStrategy,
            SecurityContextRepository contextRepository,
            BankUserRepository userRepository) {

        this.authenticationManager = authenticationManager;
        this.sessionStrategy = sessionStrategy;
        this.contextRepository = contextRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public UserResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException();
        }

        BankUser user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new InvalidCredentialsException());

        // Rotate the session ID and clear the previous CSRF token.
        sessionStrategy.onAuthentication(
                authentication, httpRequest, httpResponse);

        // Create the security context for this authenticated user.
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Persist authentication so later requests can load it.
        contextRepository.saveContext(
                context, httpRequest, httpResponse);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    //api/auth/me recognizes the user from the session cookie, without receiving the password again.
    @GetMapping("/me")
    public UserResponse currentUser(Authentication authentication) {

        BankUser user = userRepository
                //Spring supplies Authentication from the saved security context.
                // Its getName() returns the authenticated username—not a username
                // supplied in this request.
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new InvalidCredentialsException());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}