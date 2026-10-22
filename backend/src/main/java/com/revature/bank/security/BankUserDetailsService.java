package com.revature.bank.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.revature.bank.entity.BankUser;
import com.revature.bank.repository.BankUserRepository;

//- BankUser represents your database row.
//- UserDetails represents the information Spring Security needs for authentication.
//- UserDetailsService defines the method for loading that information.
//The imported User is Spring Security’s class, separate from your BankUser entity.
//This service loads the user; it doesn’t check the password itself.

@Service
public class BankUserDetailsService implements UserDetailsService {

    private final BankUserRepository userRepository;

    public BankUserDetailsService(BankUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        BankUser bankUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found."));

        return User.withUsername(bankUser.getUsername())
                //supplies the existing hash, without hashing it again:
                .password(bankUser.getPasswordHash())
                .roles("USER")
                .build();
    }
}
