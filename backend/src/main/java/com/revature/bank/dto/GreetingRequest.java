package com.revature.bank.dto;

import jakarta.validation.constraints.NotBlank;

public class GreetingRequest {

    //@NotBlank rejects null, empty strings, and strings containing only whitespace.
    @NotBlank(message = "Name is required.")
    private String name;

    public GreetingRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}