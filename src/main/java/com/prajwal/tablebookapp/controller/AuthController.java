package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.RegisterDto;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.service.UserService;
import com.prajwal.tablebookapp.service.implementations.UserPrincipal;
import com.prajwal.tablebookapp.service.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
// This controller will handle guest-related operations
public class AuthController {

    private final UserService userService;

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    // Login user -- username and password -> jwt token will be returned
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody RegisterDto user) {
        String token = userService.verifyUser(user.getEmail(), user.getPassword());
        return ResponseEntity.ok(token);
    }

}
