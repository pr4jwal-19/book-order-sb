package com.prajwal.tablebookapp.exception;

import com.prajwal.tablebookapp.dto.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(TableNotAvailableException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleTableNotAvailableException(TableNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(TableDeletionFailedException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleTableDeletionException(TableDeletionFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleReservationNotFoundException(ReservationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleReservationConflictException(ReservationConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleUnauthorizedActionException(UnauthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleAuthenticationException(AuthenticationFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message("Unauthorized access: " + ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleExpiredJwtTokenException(InvalidJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message("Token has expired: " + ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    // fallback for any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseWrapper.<Void>builder()
                        .success(false)
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .data(null)
                        .build()
        );
    }

}
