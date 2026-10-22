package com.revature.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequest {

    @NotBlank(message = "Account type is required.")
    //@Pattern accepts exactly Checking or Savings, including capitalization.
    @Pattern(
            regexp = "Checking|Savings",
            message = "Account type must be Checking or Savings."
    )
    private String accountType;

    public CreateAccountRequest() {
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
