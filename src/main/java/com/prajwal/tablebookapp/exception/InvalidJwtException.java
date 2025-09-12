package com.prajwal.tablebookapp.exception;

public class InvalidJwtException extends ApiException {
    public InvalidJwtException(String message) {
        super(message);
    }
}
