package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipoToken;
    private Long id;
    private String username;
    private String email;
    private String apellido;
    private String nombreCompleto;
    private LocalDateTime ultimoAcceso;
}
