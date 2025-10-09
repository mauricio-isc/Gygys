package com.gym.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoMembresiaResponse {
    private Long       id;
    private String     nombre;
    private String     descripcion;
    private Integer    duracionDias;
    private String     duracionFormateada;
    private Boolean    activo;
    private LocalDateTime fechaCreacion;
    private BigDecimal precio;
}