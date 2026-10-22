package com.revature.bank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.bank.dto.LoginRequest;
import com.revature.bank.dto.UserResponse;
import com.revature.bank.entity.BankUser;
import com.revature.bank.exception.InvalidCredentialsException;
import com.revature.bank.repository.BankUserRepository;

@Service
public class LoginService {

    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(
            BankUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //This method:
    //1. Finds the user by username.
    //2. Throws an exception if no user exists.
    //3. Checks the entered password against the stored hash.

    // marks this as a read-only transaction; this method
    // doesn’t save or update users.
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {

        BankUser user = userRepository
                .findByUsername(request.getUsername())
                // .orElseThrow(...) unwraps the repository’s Optional:
                // it gives us the user when present, or throws the supplied
                // exception when empty.
                .orElseThrow(() -> new InvalidCredentialsException());

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );
          // We use the same error message in InvalidCredentialsException for an unknown username and
          // an incorrect password.
        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}

//flow:
//Check credentials → Apply session protections
//    → Save security context → Return safe user details
