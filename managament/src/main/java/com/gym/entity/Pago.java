package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La membresia es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", nullable = false)
    private Membresia membresia;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El monto debe tener como maximo 8 digitos enteros y 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 15)
    private Metodopago metodoPago;

    @Size(max = 100, message = "La referencia de pago no puede exceder 100 carecteres")
    @Column(name = "referencia_pago")
    private String referenciaPago;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String notas;

    @NotNull(message = "El usuario que registro el pago es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

    public enum Metodopago{
        EFECTIVO,
        TARJETA,
        TRANSFERENCIA,
        OTRO
    }
}
