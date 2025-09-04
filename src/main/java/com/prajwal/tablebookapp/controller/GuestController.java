package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.RegisterDto;
import com.prajwal.tablebookapp.dto.ResponseWrapper;
import com.prajwal.tablebookapp.model.Role;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.service.UserService;
import com.prajwal.tablebookapp.service.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final UserService userService;

    private final JwtUtils jwtUtils;

    @Autowired
    public GuestController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
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
    }
}
