package com.prajwal.tablebookapp.exception;

public class ReservationNotFoundException extends ApiException {
    public ReservationNotFoundException(Long id) {
        super("Reservation with ID " + id + " not found.");
    }
}
