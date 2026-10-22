package com.revature.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Makes this class a JPA entity
@Entity
//Identifies its database table
@Table(name = "bank_users")
public class BankUser {
    //An entity represents database data. One BankUser object represents one row in bank_users

    //Marks the primary key
    @Id
    //Lets PostgreSQL generate the ID. We use Long for PostgreSQL’s BIGINT.
    // A new user’s ID starts as null; saving it will supply the generated ID.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Maps a field to a column
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false,
            unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false,
            columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    public BankUser() {
        // JPA needs a no-argument constructor.
    }

    public BankUser(String username, String passwordHash,
                    String firstName, String lastName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}