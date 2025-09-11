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
    public ResponseEntity<CafeTableDto> addTable(@RequestBody CafeTableDto cafeTableDto) {
        return ResponseEntity.ok(cafeTableService.addTable(cafeTableDto));
    }//tested

    @PutMapping("/updateTable/{tableId}")
    public ResponseEntity<CafeTableDto> updateTable(@PathVariable Long tableId, @RequestBody CafeTableDto cafeTableDto) {
        return ResponseEntity.ok(cafeTableService.updateTable(tableId, cafeTableDto));
    }//tested

    @DeleteMapping("/deleteTable/{tableId}")
    public ResponseEntity<String> deleteTable(@PathVariable Long tableId) {
        cafeTableService.deleteTable(tableId);
        return ResponseEntity.ok("Table deleted successfully");
    }//tested

    // view and manage(cancel) all reservations
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> viewAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }//tested

    @DeleteMapping("/cancelReservation/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok("Reservation cancelled successfully");
    }//tested

}
