package com.gym.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TipoMembresiaRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La duración en días es requerida")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracionDias;

    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    private Boolean activo;
}