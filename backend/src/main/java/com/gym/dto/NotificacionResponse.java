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
public class NotificacionResponse {

    private Long id;
    private Long miembroId;
    private String nombreMiembro;
    private String tipoNotificacion;
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private Boolean leida;
    private LocalDateTime fechaLectura;
    private Boolean enviada;
    private LocalDateTime fechaProgramada;
    private Boolean pendiente;
}