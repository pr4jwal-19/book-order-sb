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
@RequestMapping("/api/v1/admin")
// This controller will handle admin-related operations
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Users> registerAdmin(@RequestBody RegisterDto req) {
        req.setRole(Role.ADMIN);
        System.out.println("Registering admin: " + req);
        return ResponseEntity.ok(userService.registerUser(req));
    }
}
