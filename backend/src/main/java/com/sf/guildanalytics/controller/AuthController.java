package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.JwtResponse;
import com.sf.guildanalytics.dto.LoginRequest;
import com.sf.guildanalytics.dto.UserDTO;
import com.sf.guildanalytics.entity.User;
import com.sf.guildanalytics.repository.UserRepository;
import com.sf.guildanalytics.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole(), user.getPlayerId());

        return ResponseEntity.ok(new JwtResponse(jwt, userDTO));
    }
}
