package com.prajwal.tablebookapp.service;

import com.prajwal.tablebookapp.dto.RegisterDto;
import com.prajwal.tablebookapp.model.AuthProvider;
import com.prajwal.tablebookapp.model.Role;
import com.prajwal.tablebookapp.model.Users;
import com.prajwal.tablebookapp.repo.UserRepo;
import com.prajwal.tablebookapp.service.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(UserRepo userRepo, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    public Users registerUser(RegisterDto req) {

        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setAuthProvider(AuthProvider.SELF);

        return userRepo.save(user);
    }

    public String verifyUser(String email, String password) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (auth.isAuthenticated()) {
            // Generate a token
            return jwtUtils.generateToken((UserDetails) auth.getPrincipal());
        }
        else {
            throw new RuntimeException("Authentication failed");
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
        newUser.setPassword("");
        newUser.setPhoneNo(null);
        newUser.setRole(Role.GUEST);
        newUser.setAuthProvider(AuthProvider.GOOGLE);

        return userRepo.save(newUser);
    }
}
