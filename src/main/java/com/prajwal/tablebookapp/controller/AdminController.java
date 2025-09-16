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
@RequestMapping("/api/v1/admin")
// This controller will handle admin-related operations
public class AdminController {

    private final UserService userService;
    private final CafeTableService cafeTableService;
    private final ReservationService reservationService;

    private final JwtUtils jwtUtils;

    @Autowired
    public AdminController(UserService userService, JwtUtils jwtUtils, CafeTableService cafeTableService, ReservationService reservationService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.cafeTableService = cafeTableService;
        this.reservationService = reservationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> registerAdmin(@RequestBody RegisterDto req) {

        req.setRole(Role.ADMIN);
        //System.out.println("Registering admin: " + req);
        Users user = userService.registerUser(req);

        String token = jwtUtils.generateToken(
                user.getEmail(),
                Collections.singletonList(user.getRole().name())
        );

        AuthResponseDto response = AuthResponseDto.builder()
                        .token(token)
                        .user(user)
                        .build();

        System.out.println("Generated Token: " + token); // use it later -- can make ResponseDto and work with it to throw token too
        return ResponseEntity.ok(
                ResponseWrapper.<AuthResponseDto>builder()
                        .success(true)
                        .message("Admin registered successfully")
                        .data(response)
                        .build()
        );
    }//tested

    // admin flow
    // when admin logs in and clicks manage tables
    // can add, update, delete tables

    @PostMapping("/add-table")
    public ResponseEntity<ResponseWrapper<CafeTableDto>> addTable(@RequestBody CafeTableDto cafeTableDto) {
        CafeTableDto savedTable = cafeTableService.addTable(cafeTableDto);
        return ResponseEntity.ok(
                ResponseWrapper.<CafeTableDto>builder()
                        .success(true)
                        .message("Table added successfully")
                        .data(savedTable)
                        .build()
        );
    }//tested

    @PutMapping("/update-table/{tableId}")
    public ResponseEntity<ResponseWrapper<CafeTableDto>> updateTable(@PathVariable Long tableId, @RequestBody CafeTableDto cafeTableDto) {
        CafeTableDto updatedTable =
                cafeTableService.updateTable(tableId, cafeTableDto);
        return ResponseEntity.ok(
                ResponseWrapper.<CafeTableDto>builder()
                        .success(true)
                        .message("Table updated successfully")
                        .data(updatedTable)
                        .build()
        );
    }//tested

    @DeleteMapping("/delete-table/{tableId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteTable(@PathVariable Long tableId) {
        cafeTableService.deleteTable(tableId);
        return ResponseEntity.ok(
                ResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Table deleted successfully")
                        .data(null)
                        .build()
        );
    }//tested

    // view and manage(cancel) all reservations
    @GetMapping("/reservations")
    public ResponseEntity<ResponseWrapper<List<ReservationDto>>>
        viewAllReservations(@RequestParam(required = false, defaultValue = "1") @Min(1) int pageNo,
                            @RequestParam(required = false, defaultValue = "5") @Min(1) @Max(30) int pageSize,
                            @RequestParam(required = false, defaultValue = "reservationId") String sortBy,
                            @RequestParam(required = false, defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<ReservationDto> reservations = reservationService
                .getAllReservations(
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
        );
    }//tested

    @DeleteMapping("/cancel-reservation/{reservationId}")
    public ResponseEntity<ResponseWrapper<Void>> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok(
                ResponseWrapper.<Void>builder()
                        .success(true)
                        .message("Reservation cancelled successfully")
                        .data(null)
                        .build()
        );
    }//tested

}
