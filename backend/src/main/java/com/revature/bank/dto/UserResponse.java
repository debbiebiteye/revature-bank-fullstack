package com.revature.bank.dto;

public class UserResponse {
    private final Long id;
    private final String username;
    private final String firstName;
    private final String lastName;

    public UserResponse(Long id, String username,
                        String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

// The fields are final: the constructor sets them, and
// they cannot be reassigned afterward. Getters let Spring’s
// JSON converter read them; setters aren’t needed for this
// outgoing response.

//There is no password or password hash in this class,
// so neither will appear in its JSON response.
//The fields are final: the constructor sets them,
// and they cannot be reassigned afterward. Getters
// let Spring’s JSON converter read them; setters aren’t
// needed for this outgoing response.

//The response uses UserResponse, which contains neither the password nor
// its hash.
//Hashing protects the stored password. When deployed, HTTPS protects the
// request—including the entered password—as it travels to the server.