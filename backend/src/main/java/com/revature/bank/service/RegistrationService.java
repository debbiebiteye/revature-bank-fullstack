package com.revature.bank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.bank.dto.RegistrationRequest;
import com.revature.bank.dto.UserResponse;
import com.revature.bank.entity.BankUser;
import com.revature.bank.exception.DuplicateUsernameException;
import com.revature.bank.repository.BankUserRepository;

@Service
public class RegistrationService {

    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //Constructor injection supplies both dependencies.
    public RegistrationService(
            BankUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //The  UserResponse method follows four steps:
    //1. Check whether the username exists.
    //2. Hash the entered password.
    //3. Save the entity.
    //4. Return a DTO containing safe user details.
    //@Transactional groups the database work into a transaction
    // that commits on success and rolls back on an unchecked exception.
    @Transactional
    public UserResponse register(RegistrationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException();
        }

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        BankUser user = new BankUser(
                request.getUsername(),
                passwordHash,
                request.getFirstName(),
                request.getLastName()
        );

        BankUser savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }
}

