
    package com.revature.bank.exception;

    public class DuplicateAccountException extends RuntimeException {

        public DuplicateAccountException() {
            super("You already have an account of this type.");
        }
    }

