package com.revature.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.bank.entity.BankUser;


//This is the database-access layer
public interface BankUserRepository
        extends JpaRepository<BankUser, Long> {

    //finds a user by the entity’s username field.

    Optional<BankUser> findByUsername(String username);
    //Optional<BankUser> represents either a found user or an empty result,
    // making the possibility of “not found” explicit.
    //returns true if that username exists.
    boolean existsByUsername(String username);
}