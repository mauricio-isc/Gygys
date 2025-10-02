package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiembroResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String direccion;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    private String notas;
    private String documentoIdentidad;
    private String genero;
    private String fotoUrl;
    private String nombreCompleto;
    private Integer edad;
    private Boolean tieneMembresiaActiva;
    private MembresiaResponse membresiaActiva;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembresiaResponse{
        private Long id;
        private String nombre;
        private String descripcion;
        private Integer duracionDias;
        private String duracionFormateada;
        private java.math.BigDecimal precio;
    }
}
