package com.prajwal.tablebookapp.exception;

public class TableNotAvailableException extends ApiException {
    public TableNotAvailableException(Long tableId) {
        super("Table number " + tableId + " is not available for booking" );
    }
}
