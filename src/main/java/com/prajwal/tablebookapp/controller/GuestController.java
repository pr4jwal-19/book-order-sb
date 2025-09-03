package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.RegisterDto;
import com.prajwal.tablebookapp.model.Role;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final UserService userService;

    @Autowired
    public GuestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Users> registerGuest(@RequestBody RegisterDto req) {
        req.setRole(Role.GUEST);
        return ResponseEntity.ok(userService.registerUser(req));
    }
}
