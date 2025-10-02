package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaResponse {

    private Long id;
    private Long miembroId;
    private String nombreMiembro;
    private String tipoMembresia;
    private String nombreTipoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private BigDecimal precioPagado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Long creadoPor;
    private String nombreCreador;
    private Long diasRestantes;
    private Boolean vencida;
    private Boolean vencePronto;
    private Integer diasParaVencimiento;
}
