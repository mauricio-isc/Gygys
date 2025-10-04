package com.gym.dto;

import lombok.*;

import java.math.BigDecimal;

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
    private BigDecimal precio;
}