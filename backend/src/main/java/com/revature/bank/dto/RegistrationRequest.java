package com.revature.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationRequest {

    @NotBlank(message = "Username is required.")
    @Size(max = 50, message = "Username must be at most 50 characters.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 15, max = 128,
            message = "Password must be between 15 and 128 characters.")
    private String password;

    @NotBlank(message = "First name is required.")
    @Size(max = 100, message = "First name must be at most 100 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 100, message = "Last name must be at most 100 characters.")
    private String lastName;

    public RegistrationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

//Notice the distinction:
//- Request DTO: contains the entered password.The password travels from the
// input form to the request DTO inside the HTTP request’s JSON body
//- Entity: contains passwordHash, which will be stored in PostgreSQL.
//- Response DTO: will contain neither.