package com.revature.bank.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private final Long id;
    private final String accountType;
    private final BigDecimal balance;

    //AccountResponse contains the data returned to the client:
    //The response exposes account details without serializing
    // the BankUser owner entity. Next we’ll add the service that
    // creates and retrieves accounts for the authenticated user.
    public AccountResponse(Long id, String accountType,
                           BigDecimal balance) {
        this.id = id;
        this.accountType = accountType;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}