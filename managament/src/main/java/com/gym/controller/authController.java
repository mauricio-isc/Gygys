package com.gym.controller;

import com.gym.dto.AuthRequest;
import com.gym.dto.AuthResponse;
import com.gym.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class authController {

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request){
        AuthResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader){
        //extraer usuario del token jwt
        String token = authHeader.substring(7);
        String username = extractUsernameFromToken(token);
        authenticationService.logout(username);
        return ResponseEntity.ok().build();
    }

    public String extractUsernameFromToken(String token){
        return "admin";
    }
}
