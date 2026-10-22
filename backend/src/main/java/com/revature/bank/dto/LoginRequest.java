package com.revature.bank.dto;

import jakarta.validation.constraints.NotBlank;


//Registration and login use separate DTOs because registration also needs
// first and last names; login only needs credentials.
public class LoginRequest {
    //Checks that credentials were supplied. It doesn’t determine whether they’re correct
    // - that is the login service’s job.
    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Password is required.")
    private String password;

    public LoginRequest() {
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
}
