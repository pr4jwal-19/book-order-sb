package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.LoginDto;
import com.prajwal.tablebookapp.dto.ResponseWrapper;
import com.prajwal.tablebookapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
// This controller will handle guest-related operations
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Login user -- username and password -> jwt token will be returned
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<String>> login(@RequestBody LoginDto user) {
        String token = userService.verifyUser(user.getEmail(), user.getPassword());
        ResponseWrapper<String> res = new ResponseWrapper<>(true, "User logged in successfully", token);
        return ResponseEntity.ok(res);
    }

}
