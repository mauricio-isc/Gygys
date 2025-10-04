package com.gym.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
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

}