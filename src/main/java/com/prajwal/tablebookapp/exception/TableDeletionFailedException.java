package com.prajwal.tablebookapp.exception;

public class TableDeletionFailedException extends ApiException {
    public TableDeletionFailedException(String tableNo) {
        super("Table " + tableNo + " cannot be deleted as it has active reservations or bookings.");
    }
}
