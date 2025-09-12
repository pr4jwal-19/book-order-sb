package com.prajwal.tablebookapp.exception;

public class AuthenticationFailedException extends ApiException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
