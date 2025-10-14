package com.gym.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PagoDetalleResponse {
    private Long id;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String referenciaPago;
    private String notas;
    private String nombreMiembro;
    private String tipoMembresia;
    private LocalDate fechaInicioMembresia;
    private LocalDate fechaFinMembresia;
    private String registradoPor;
}