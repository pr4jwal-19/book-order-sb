package com.prajwal.tablebookapp.service;

import com.prajwal.tablebookapp.dto.RegisterDto;
import com.prajwal.tablebookapp.exception.AuthenticationFailedException;
import com.prajwal.tablebookapp.exception.UserNotFoundException;
import com.prajwal.tablebookapp.helper.AppConstants;
import com.prajwal.tablebookapp.model.AuthProvider;
import com.prajwal.tablebookapp.model.Role;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.model.VerificationToken;
import com.prajwal.tablebookapp.repo.UserRepo;
import com.prajwal.tablebookapp.repo.VerificationTokenRepo;
import com.prajwal.tablebookapp.service.utils.EmailNotificationService;
import com.prajwal.tablebookapp.service.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepo userRepo;

    private final JwtUtils jwtUtils;

    private final VerificationTokenRepo verificationTokenRepo;

    private final EmailNotificationService emailNotificationService;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(UserRepo userRepo, AuthenticationManager authenticationManager, JwtUtils jwtUtils, VerificationTokenRepo verificationTokenRepo, EmailNotificationService emailNotificationService) {
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.verificationTokenRepo = verificationTokenRepo;
        this.emailNotificationService = emailNotificationService;
    }


    public Users registerUser(RegisterDto req) {

        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setPhoneNo(req.getPhoneNo());
        user.setRole(req.getRole());
        user.setAuthProvider(AuthProvider.SELF);
        user.setUserVerified(false); // initially not verified

        System.out.println("Registering user: " + user);
        Users savedUser = userRepo.save(user);

        // Generate -> verification token
        String vToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(vToken);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(AppConstants.EXPIRY_DATE);

        verificationTokenRepo.save(verificationToken);

        // Send email with link -> after deploy -- change the link
        String verificationLink = AppConstants.APP_BASE_URL + "/auth/verify?token=" + vToken;
        emailNotificationService.sendReservationReminder(
                savedUser.getEmail(),
                "Verify your email",
                "Click the link to verify your email: <a href=\"" + verificationLink + "\">Verify Email</a>"
        );

        return savedUser;

    }

    public Users getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public String verifyUser(String email, String password) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (auth.isAuthenticated()) {
            // Generate a token
            return jwtUtils.generateToken((UserDetails) auth.getPrincipal());
        }
        else {
            throw new AuthenticationFailedException("Invalid Credentials");
        }
    }

    public Users findOrRegisterUser(String email) {
        // Check if user exists
        Optional<Users> existingUser = userRepo.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        // If not, register the user
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setUsername(email.split("@")[0]);
        newUser.setPassword(null); // no password for OAuth users
        newUser.setPhoneNo(null);
        newUser.setRole(Role.GUEST);
        newUser.setUserVerified(true);
        newUser.setAuthProvider(AuthProvider.GOOGLE);

        return userRepo.save(newUser);
    }
}
