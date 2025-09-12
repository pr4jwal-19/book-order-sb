package com.prajwal.tablebookapp.exception;

public class ReservationConflictException extends ApiException {
    public ReservationConflictException(Long tableId) {
        super("Table number " + tableId + " already has a reservation at the requested time" );
    }
}
