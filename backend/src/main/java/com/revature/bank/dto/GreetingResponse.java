package com.revature.bank.dto;


//The constructor sets the message.
public class GreetingResponse {
    private final String message;

    public GreetingResponse(String message) {
        this.message = message;
    }

    //The getter exposes it for conversion to JSON.
    public String getMessage() {
        return message;
    }
}