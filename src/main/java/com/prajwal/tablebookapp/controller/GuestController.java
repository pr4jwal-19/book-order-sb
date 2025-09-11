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
    public ResponseEntity<List<CafeTableDto>> viewAllTables() {
        return ResponseEntity.ok(cafeTableService.getAllTables());
    }//tested

    // if table status -> green/available -> can book
    @PostMapping("/bookTable")
    public ResponseEntity<ReservationDto> bookTable(@RequestBody BookTableRequest tableRequest) {
        return ResponseEntity.ok(reservationService.bookTable(tableRequest));
    }//tested

    // if clicks on view reservations
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> viewReservations() {
        return ResponseEntity.ok(reservationService.getReservationsFromCurrentUser()); // start from here
    }//tested

}
