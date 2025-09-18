package com.prajwal.tablebookapp.controller;

import com.prajwal.tablebookapp.dto.AuthResponseDto;
import com.prajwal.tablebookapp.dto.LoginDto;
import com.prajwal.tablebookapp.dto.ResponseWrapper;
import com.prajwal.tablebookapp.exception.AuthenticationFailedException;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.model.VerificationToken;
import com.prajwal.tablebookapp.repo.UserRepo;
import com.prajwal.tablebookapp.repo.VerificationTokenRepo;
import com.prajwal.tablebookapp.service.UserService;
import com.prajwal.tablebookapp.service.utils.AuthResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
// This controller will handle guest-related operations
public class AuthController {

    private final UserService userService;
    private final UserRepo userRepo;

    private final VerificationTokenRepo verificationTokenRepo;

    @Autowired
    public AuthController(UserService userService, UserRepo userRepo, VerificationTokenRepo verificationTokenRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.verificationTokenRepo = verificationTokenRepo;
    }

    // Login user -- username and password -> jwt token will be returned
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> login(@RequestBody LoginDto user) {

        Users loggedInUser = userService.getUserByEmail(user.getEmail());

        if (!loggedInUser.isUserVerified()) {
            return ResponseEntity.ok(
                    ResponseWrapper.<AuthResponseDto>builder()
                            .success(false)
                            .message("User is not verified. Please verify your email before logging in.")
                            .data(null)
                            .build()
            );
        }

        // user is verified from here
        String token = userService.verifyUser(user.getEmail(), user.getPassword());

        AuthResponseDto response = AuthResponseBuilder.buildAuthResponseDto(loggedInUser, token);

        return ResponseEntity.ok(
                ResponseWrapper.<AuthResponseDto>builder()
                        .success(true)
                        .message("User logged in successfully")
                        .data(response)
                        .build()
        );

    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseWrapper<String>> verifyEmail(@RequestParam("token") String token) {

        VerificationToken vToken = verificationTokenRepo.findByToken(token)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid email verification token"));

        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepo.delete(vToken); // delete expired token
            return ResponseEntity.badRequest().body(
                    ResponseWrapper.<String>builder()
                            .success(false)
                            .message("Token has expired. Please register again.")
                            .data(null)
                            .build()
            );
        }

        Users user = vToken.getUser();
        user.setUserVerified(true);
        userRepo.save(user); // update user as verified

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .success(true)
                        .message("Email verified.")
                        .data("Your email has been verified successfully. You can now log in.")
                        .build()
        );
    }

}
