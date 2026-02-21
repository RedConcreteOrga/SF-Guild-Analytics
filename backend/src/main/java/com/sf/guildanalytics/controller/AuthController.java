package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.JwtResponse;
import com.sf.guildanalytics.dto.LoginRequest;
import com.sf.guildanalytics.dto.UserDTO;
import com.sf.guildanalytics.entity.User;
import com.sf.guildanalytics.repository.UserRepository;
import com.sf.guildanalytics.security.JwtUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole(), user.getPlayerId());

            // SECURITY: Only log the username, never the password
            log.info("Successful login for user: {}", loginRequest.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt, userDTO));

        } catch (BadCredentialsException e) {
            // SECURITY: Generic error message — never reveal whether username or password was wrong.
            // This prevents username enumeration attacks.
            // SECURITY: Log only at WARN level without exposing the attempted password.
            log.warn("Failed login attempt for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid username or password\"}");
        }
    }
}
