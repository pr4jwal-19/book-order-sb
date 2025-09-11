package com.prajwal.tablebookapp.exception;

public class ReservationConflictException extends ApiException {
    public ReservationConflictException(Long tableNo) {
        super("Table number " + tableNo + " already has a reservation at the requested time" );
    }
}
