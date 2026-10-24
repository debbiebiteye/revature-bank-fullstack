package com.revature.bank.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.revature.bank.entity.BankAccount;
import com.revature.bank.entity.BankUser;
import com.revature.bank.repository.BankAccountRepository;
import com.revature.bank.repository.BankUserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private BankUserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void withdrawalExceedingBalanceIsRejected() {
        // Arrange: a user owns an account containing $20.00.
        BankUser user = new BankUser();
        user.setId(1L);
        user.setUsername("demo");

        BankAccount account = new BankAccount(user, "Checking");
        account.setId(10L);
        account.setBalance(new BigDecimal("20.00"));

        when(userRepository.findByUsername("demo"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findOwnedAccountForUpdate(10L, 1L))
                .thenReturn(Optional.of(account));

        // Act: attempt to withdraw $25.00.
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(
                        "demo", 10L, new BigDecimal("25.00"))
        );

        // Assert: the operation failed and the balance stayed unchanged.
        assertEquals("Insufficient funds.", exception.getMessage());

        assertEquals(
                new BigDecimal("20.00"),
                account.getBalance()
        );
    }

    @Test
    void withdrawalWithinBalanceUpdatesBalance() {
        // Arrange
        BankUser user = new BankUser();
        user.setId(1L);
        user.setUsername("demo");

        BankAccount account = new BankAccount(user, "Checking");
        account.setId(10L);
        account.setBalance(new BigDecimal("20.00"));

        when(userRepository.findByUsername("demo"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findOwnedAccountForUpdate(10L, 1L))
                .thenReturn(Optional.of(account));

        // Act
        var response = accountService.withdraw(
                "demo", 10L, new BigDecimal("5.00"));

        // Assert
        assertEquals(
                new BigDecimal("15.00"),
                response.getBalance()
        );

        assertEquals(
                new BigDecimal("15.00"),
                account.getBalance()
        );

        assertEquals(10L, response.getId().longValue());
    }

    @Test
    void withdrawalFromUnavailableAccountIsRejected() {
        // Arrange: the signed-in user exists.
        BankUser user = new BankUser();
        user.setId(2L);
        user.setUsername("second-user");

        when(userRepository.findByUsername("second-user"))
                .thenReturn(Optional.of(user));

        // No account matches BOTH account ID 10 and owner ID 2.
        when(accountRepository.findOwnedAccountForUpdate(10L, 2L))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(
                        "second-user", 10L, new BigDecimal("5.00"))
        );

        // Assert
        assertEquals("Account not found.", exception.getMessage());

        verify(accountRepository)
                .findOwnedAccountForUpdate(10L, 2L);

        verify(accountRepository, never()).findById(anyLong());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-1.00", "1.234"})
    void withdrawalWithInvalidAmountIsRejected(String input) {
        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(
                        "demo", 10L, new BigDecimal(input))
        );

        // Invalid amounts should be rejected before database access.
        verifyNoInteractions(userRepository, accountRepository);
    }

    @Test
    void depositIncreasesBalance() {
        // Arrange
        BankUser user = new BankUser();
        user.setId(1L);
        user.setUsername("demo");

        BankAccount account = new BankAccount(user, "Checking");
        account.setId(10L);
        account.setBalance(new BigDecimal("20.00"));

        when(userRepository.findByUsername("demo"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findOwnedAccountForUpdate(10L, 1L))
                .thenReturn(Optional.of(account));

        // Act
        var response = accountService.deposit(
                "demo", 10L, new BigDecimal("5.50"));

        // Assert
        assertEquals(
                new BigDecimal("25.50"),
                account.getBalance()
        );

        assertEquals(
                new BigDecimal("25.50"),
                response.getBalance()
        );

        assertEquals(10L, response.getId().longValue());
    }

    @Test
    void depositExceedingBalanceLimitIsRejected() {
        // Arrange: the account is one cent below the limit.
        BankUser user = new BankUser();
        user.setId(1L);
        user.setUsername("demo");

        BankAccount account = new BankAccount(user, "Checking");
        account.setId(10L);
        account.setBalance(new BigDecimal("9999999999.98"));

        when(userRepository.findByUsername("demo"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findOwnedAccountForUpdate(10L, 1L))
                .thenReturn(Optional.of(account));

        // Act: two more cents would exceed the limit.
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deposit(
                        "demo", 10L, new BigDecimal("0.02"))
        );

        // Assert
        assertEquals(
                "Deposit would exceed the balance limit.",
                exception.getMessage()
        );

        assertEquals(
                new BigDecimal("9999999999.98"),
                account.getBalance()
        );
    }

    @Test
    void depositReachingExactBalanceLimitIsAllowed() {
        // Arrange
        BankUser user = new BankUser();
        user.setId(1L);
        user.setUsername("demo");

        BankAccount account = new BankAccount(user, "Checking");
        account.setId(10L);
        account.setBalance(new BigDecimal("9999999999.98"));

        when(userRepository.findByUsername("demo"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findOwnedAccountForUpdate(10L, 1L))
                .thenReturn(Optional.of(account));

        // Act: one cent reaches the limit exactly.
        var response = accountService.deposit(
                "demo", 10L, new BigDecimal("0.01"));

        // Assert
        BigDecimal expected = new BigDecimal("9999999999.99");

        assertEquals(expected, account.getBalance());
        assertEquals(expected, response.getBalance());
    }

    @Test
    void withdrawingEntireBalanceLeavesZero() {
        // Arrange
        BankUser user = new BankUser();
        user.setId(1L);
        user.setUsername("demo");

        BankAccount account = new BankAccount(user, "Checking");
        account.setId(10L);
        account.setBalance(new BigDecimal("20.00"));

        when(userRepository.findByUsername("demo"))
                .thenReturn(Optional.of(user));

        when(accountRepository.findOwnedAccountForUpdate(10L, 1L))
                .thenReturn(Optional.of(account));

        // Act
        var response = accountService.withdraw(
                "demo", 10L, new BigDecimal("20.00"));

        // Assert
        BigDecimal expected = new BigDecimal("0.00");

        assertEquals(expected, account.getBalance());
        assertEquals(expected, response.getBalance());
    }
}