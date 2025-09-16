package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.*;
import com.prajwal.tablebookapp.model.Role;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.service.CafeTableService;
import com.prajwal.tablebookapp.service.ReservationService;
import com.prajwal.tablebookapp.service.UserService;
import com.prajwal.tablebookapp.service.utils.JwtUtils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final UserService userService;
    private final CafeTableService cafeTableService;
    private final ReservationService reservationService;

    private final JwtUtils jwtUtils;

    @Autowired
    public GuestController(UserService userService, JwtUtils jwtUtils, CafeTableService cafeTableService, ReservationService reservationService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.cafeTableService = cafeTableService;
        this.reservationService = reservationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> registerGuest(@RequestBody RegisterDto req) {

        req.setRole(Role.GUEST);

        Users user = userService.registerUser(req);

        String token = jwtUtils.generateToken(
                user.getEmail(),
                Collections.singletonList(user.getRole().name())
        );

        AuthResponseDto response = AuthResponseDto.builder()
                .token(token)
                .user(user)
                .build();

        System.out.println("Generated Token: " + token);
        return ResponseEntity.ok(
                ResponseWrapper.<AuthResponseDto>builder()
                        .success(true)
                        .message("Guest registered successfully")
                        .data(response)
                        .build()
        );
    }//tested

    // guest flow
    // when guest logs in and clicks browse tables
    @GetMapping("/tables")
    public ResponseEntity<ResponseWrapper<List<CafeTableDto>>>
        viewAllTables(@RequestParam(required = false, defaultValue = "1") @Min(1) int pageNo,
                      @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(20) int pageSize,
                      @RequestParam(required = false, defaultValue = "tableId") String sortBy,
                      @RequestParam(required = false, defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<CafeTableDto> tables = cafeTableService
                .getAllTables(
                        PageRequest.of(pageNo - 1, pageSize, sort)
                );
        return ResponseEntity.ok(
                ResponseWrapper.<List<CafeTableDto>>builder()
                        .success(true)
                        .message("Fetched all tables successfully")
                        .data(tables.getContent())
                        .pageNo(tables.getNumber() + 1)
                        .pageSize(tables.getSize())
                        .totalElements(tables.getTotalElements())
                        .totalPages(tables.getTotalPages())
                        .last(tables.isLast())
                        .build()
        );
    }//tested

    // if table status -> green/available -> can book
    @PostMapping("/book-table")
    public ResponseEntity<ResponseWrapper<ReservationDto>> bookTable(@RequestBody BookTableRequest tableRequest) {
        ReservationDto reservationDto = reservationService.bookTable(tableRequest);
        return ResponseEntity.ok(
                ResponseWrapper.<ReservationDto>builder()
                        .success(true)
                        .message("Table booked successfully")
                        .data(reservationDto)
                        .build()
        );
    }//tested

    // if clicks on view reservations
    @GetMapping("/my-reservations")
    public ResponseEntity<ResponseWrapper<List<ReservationDto>>>
        viewReservations(@RequestParam(required = false, defaultValue = "1") @Min(1) int pageNo,
                         @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(20) int pageSize,
                         @RequestParam(required = false, defaultValue = "reservationId") String sortBy,
                         @RequestParam(required = false, defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<ReservationDto> reservations = reservationService
                .getReservationsFromCurrentUser(
                        PageRequest.of(pageNo - 1, pageSize, sort)
                );
        return ResponseEntity.ok(
                ResponseWrapper.<List<ReservationDto>>builder()
                        .success(true)
                        .message("Fetched all reservations successfully")
                        .data(reservations.getContent())
                        .pageNo(reservations.getNumber() + 1)
                        .pageSize(reservations.getSize())
                        .totalElements(reservations.getTotalElements())
                        .totalPages(reservations.getTotalPages())
                        .last(reservations.isLast())
                        .build()
        ); // start from here
    }//tested

}
