package com.revature.bank.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(
        name = "bank_accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "bank_accounts_user_type_key",
                columnNames = {"user_id", "account_type"}
        )
)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private BankUser owner;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @Column(name = "balance", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal balance = new BigDecimal("0.00");

    public BankAccount() {
        // Required by JPA.
    }

    public BankAccount(BankUser owner, String accountType) {
        this.owner = owner;
        this.accountType = accountType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankUser getOwner() {
        return owner;
    }

    public void setOwner(BankUser owner) {
        this.owner = owner;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
