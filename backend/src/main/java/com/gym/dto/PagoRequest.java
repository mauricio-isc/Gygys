package com.gym.dto;

import com.gym.entity.Pago;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequest {
    private Long membresiaId;
    private BigDecimal monto;
    private Pago.Metodopago metodoPago;
    private String referenciaPago;
    private String notas;
}