package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.*;
import com.prajwal.tablebookapp.model.Role;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.service.CafeTableService;
import com.prajwal.tablebookapp.service.ReservationService;
import com.prajwal.tablebookapp.service.UserService;
import com.prajwal.tablebookapp.service.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
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
    public ResponseEntity<ResponseWrapper<Users>> registerGuest(@RequestBody RegisterDto req) {

        req.setRole(Role.GUEST);

        Users user = userService.registerUser(req);

        String token = jwtUtils.generateToken(
                user.getEmail(),
                Collections.singletonList(user.getRole().name())
        );
        System.out.println("Generated Token: " + token);
        ResponseWrapper<Users> res = new ResponseWrapper<>(true, "Guest registered successfully", user);

        return ResponseEntity.ok(res);
    }//tested

    // guest flow
    // when guest logs in and clicks browse tables
    @GetMapping("/tables")
    public ResponseEntity<ResponseWrapper<List<CafeTableDto>>> viewAllTables() {
        List<CafeTableDto> tables = cafeTableService.getAllTables();
        return ResponseEntity.ok(
                ResponseWrapper.<List<CafeTableDto>>builder()
                        .success(true)
                        .message("Fetched all tables successfully")
                        .data(tables)
                        .build()
        );
    }//tested

    // if table status -> green/available -> can book
    @PostMapping("/bookTable")
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
    @GetMapping("/reservations")
    public ResponseEntity<ResponseWrapper<List<ReservationDto>>> viewReservations() {
        List<ReservationDto> reservations = reservationService.getReservationsFromCurrentUser();
        return ResponseEntity.ok(
                ResponseWrapper.<List<ReservationDto>>builder()
                        .success(true)
                        .message("Fetched all reservations successfully")
                        .data(reservations)
                        .build()
        ); // start from here
    }//tested

}
