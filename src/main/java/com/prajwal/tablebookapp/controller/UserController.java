package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.ProfileDto;
import com.prajwal.tablebookapp.dto.ResponseWrapper;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<ProfileDto>> getProfileForCurrUser(Authentication auth) {

        String email = auth.getName();
        Users user = userService.getUserByEmail(email);

        ProfileDto data = ProfileDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .address(user.getAddress())
                .build();

        return ResponseEntity.ok(
                ResponseWrapper.<ProfileDto>builder()
                        .success(true)
                        .message("User profile fetched successfully")
                        .data(data)
                        .build()
        );
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ResponseWrapper<ProfileDto>> updateProfileForCurrUser(
            Authentication auth,
            @RequestBody ProfileDto profileDto
    ) {

        String email = auth.getName();
        ProfileDto updatedProfile = userService.updateUserProfile(email, profileDto);
        return ResponseEntity.ok(
                ResponseWrapper.<ProfileDto>builder()
                        .success(true)
                        .message("User profile updated successfully")
                        .data(updatedProfile)
                        .build()
        );
    }
}
