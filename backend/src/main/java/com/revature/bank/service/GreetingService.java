package com.revature.bank.service;

import org.springframework.stereotype.Service;

//@Service tells Spring to create and manage an instance of this class.
// Its method handles the greeting logic.
@Service
public class GreetingService {

    public String createGreeting(String name) {
        return "Welcome to Revature Bank, " + name + "!";
    }
}
