package com.revature.bank.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revature.bank.dto.AccountResponse;
import com.revature.bank.dto.CreateAccountRequest;
import com.revature.bank.service.AccountService;
import com.revature.bank.dto.AmountRequest;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            Authentication authentication,
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse response = accountService.createAccount(
                authentication.getName(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    //Spring supplies the authenticated user. The client doesn’t choose whose
    // accounts to access.
    @GetMapping
    public List<AccountResponse> getAccounts(
            Authentication authentication) {

        return accountService.getAccounts(authentication.getName());
    }

    //The URL supplies the account ID, JSON supplies the amount, and
    // authentication supplies the username.
    @PostMapping("/{accountId}/deposits")
    public AccountResponse deposit(
            Authentication authentication,
            @PathVariable("accountId") Long accountId,
            @Valid @RequestBody AmountRequest request) {

        return accountService.deposit(
                authentication.getName(),
                accountId,
                request.getAmount()
        );
    }

    @PostMapping("/{accountId}/withdrawals")
    public AccountResponse withdraw(
            Authentication authentication,
            @PathVariable("accountId") Long accountId,
            @Valid @RequestBody AmountRequest request) {

        return accountService.withdraw(
                authentication.getName(),
                accountId,
                request.getAmount()
        );
    }
}