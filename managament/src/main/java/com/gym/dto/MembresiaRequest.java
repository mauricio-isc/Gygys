package com.gym.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaRequest {

    @NotNull(message = "El ID del miembro es requerido")
    private Long miembroId;

    @NotNull(message = "El ID del tipo de membresia es requerido")
    private Long tipoMembresiaId;

    @NotNull(message = "La fecha de inicio es requerida")
    @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
    private LocalDate fechaInicio;

    @NotNull(message = "el precio pagado es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio pagado debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener como maximo 8 digitos enteros y 2 decimales")
    private BigDecimal precioPagado;

    private String notas;
}
