package com.revature.bank.service;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.bank.dto.AccountResponse;
import com.revature.bank.dto.CreateAccountRequest;
import com.revature.bank.entity.BankAccount;
import com.revature.bank.entity.BankUser;
import com.revature.bank.exception.DuplicateAccountException;
import com.revature.bank.exception.InvalidCredentialsException;
import com.revature.bank.repository.BankAccountRepository;
import com.revature.bank.repository.BankUserRepository;

@Service
public class AccountService {

    private final BankAccountRepository accountRepository;
    private final BankUserRepository userRepository;

    public AccountService(
            BankAccountRepository accountRepository,
            BankUserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
   //createAccount() finds the owner, checks for a duplicate type, saves an account, and returns its DTO.
    @Transactional
    public AccountResponse createAccount(
            String username, CreateAccountRequest request) {

        BankUser owner = findUser(username);

        boolean alreadyExists =
                accountRepository.existsByOwner_IdAndAccountType(
                        owner.getId(), request.getAccountType());

        if (alreadyExists) {
            throw new DuplicateAccountException();
        }

        BankAccount account =
                new BankAccount(owner, request.getAccountType());

        BankAccount savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }

   //getAccounts() retrieves only that owner’s accounts and converts each into a DTO.
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccounts(String username) {

        BankUser owner = findUser(username);

        return accountRepository
                .findByOwner_IdOrderByIdAsc(owner.getId())
                .stream()
                //map transforms each BankAccount into an AccountResponse;
                .map(account -> toResponse(account))
                .toList(); //toList() collects those responses.
    }

    private BankUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException());
    }

    private AccountResponse toResponse(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountType(),
                account.getBalance()
        );
    }

    @Transactional // keeps the account locked until the transaction ends
    public AccountResponse deposit(
            String username, Long accountId, BigDecimal amount) {

        // Validate before accessing the database.
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        BigDecimal depositAmount;

        try {
            depositAmount = amount.setScale(2);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Amount must use whole cents.");
        }

        BankUser owner = findUser(username);

        // Lock only an account belonging to this user.
        BankAccount account = accountRepository
                .findOwnedAccountForUpdate(accountId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found."));

        BigDecimal newBalance =
                //Calculates a new BigDecimal; it doesn’t modify the old value
                account.getBalance().add(depositAmount);

        if (newBalance.compareTo(
                new BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException(
                    "Deposit would exceed the balance limit.");
        }
        //Updates the entity
        //JPA dirty checking detects that change and writes it to
        // PostgreSQL when the transaction commits. An explicit
        // save() isn’t needed because the entity was loaded within this transaction
        account.setBalance(newBalance);

        return toResponse(account);
    }

    @Transactional
    public AccountResponse withdraw(
            String username, Long accountId, BigDecimal amount) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        BigDecimal withdrawalAmount;

        try {
            withdrawalAmount = amount.setScale(2);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Amount must use whole cents.");
        }

        BankUser owner = findUser(username);

        BankAccount account = accountRepository
                .findOwnedAccountForUpdate(accountId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found."));
        //compares numerical values. A result below zero means the balance is
        // less than the requested withdrawal.
        //The account stays locked while we check and subtract. JPA saves
        // the changed balance when the transaction commits.
        if (account.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        BigDecimal newBalance =
                account.getBalance().subtract(withdrawalAmount);

        account.setBalance(newBalance);

        return toResponse(account);
    }
}


//The controller will supply username from authentication,
// not from client input. The database’s unique constraint
// remains the final protection against simultaneous duplicate creations.