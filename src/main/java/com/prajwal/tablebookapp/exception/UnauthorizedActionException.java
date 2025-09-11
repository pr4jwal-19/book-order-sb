package com.prajwal.tablebookapp.exception;

public class UnauthorizedActionException extends ApiException {
    public UnauthorizedActionException(String action) {
        super("Unauthorized action: " + action);
    }
}
