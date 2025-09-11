package com.prajwal.tablebookapp.exception;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}
