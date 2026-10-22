package com.revature.bank.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.bank.dto.RegistrationRequest;
import com.revature.bank.dto.UserResponse;
import com.revature.bank.service.RegistrationService;

//The class mapping and method mapping combine to make POST /api/users.
// A successful registration returns 201 Created, because it creates a
// new user.

@RestController
@RequestMapping("/api/users")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
            RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegistrationRequest request) {

        UserResponse response =
                registrationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}