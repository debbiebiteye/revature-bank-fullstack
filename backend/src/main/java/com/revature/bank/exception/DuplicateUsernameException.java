package com.revature.bank.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException() {
        super("That username is already taken.");
    }
}