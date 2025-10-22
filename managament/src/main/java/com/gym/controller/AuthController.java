package com.gym.controller;

import com.gym.dto.AuthRequest;
import com.gym.dto.AuthResponse;
import com.gym.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        log.info("AuthController - ENTRADA - username: {}", request.getUsername());
        AuthResponse response = authenticationService.authenticate(request);
        log.info("AuthController - SALIDA - username: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = extractUsernameFromToken(token);
        authenticationService.logout(username);
        return ResponseEntity.ok().build();
    }

    public String extractUsernameFromToken(String token) {
        return "admin";
    }
}