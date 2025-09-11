package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.CafeTableDto;
import com.prajwal.tablebookapp.dto.RegisterDto;
import com.prajwal.tablebookapp.dto.ReservationDto;
import com.prajwal.tablebookapp.dto.ResponseWrapper;
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
    public ResponseEntity<ResponseWrapper<Users>> registerAdmin(@RequestBody RegisterDto req) {

        req.setRole(Role.ADMIN);
        //System.out.println("Registering admin: " + req);
        Users user = userService.registerUser(req);

        String token = jwtUtils.generateToken(
                user.getEmail(),
                Collections.singletonList(user.getRole().name())
        );
        System.out.println("Generated Token: " + token); // use it later -- can make ResponseDto and work with it to throw token too
        ResponseWrapper<Users> res = new ResponseWrapper<>(true, "Admin registered successfully", user);
        return ResponseEntity.ok(res);
    }//tested

    // admin flow
    // when admin logs in and clicks manage tables
    // can add, update, delete tables

    @PostMapping("/addTable")
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

    @PutMapping("/updateTable/{tableId}")
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

    @DeleteMapping("/deleteTable/{tableId}")
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
    public ResponseEntity<ResponseWrapper<List<ReservationDto>>> viewAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(
                ResponseWrapper.<List<ReservationDto>>builder()
                        .success(true)
                        .message("Fetched all reservations successfully")
                        .data(reservations)
                        .build()
        );
    }//tested

    @DeleteMapping("/cancelReservation/{reservationId}")
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
