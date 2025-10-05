package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipos_membresia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoMembresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no debe de exceder de 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "La duracion en dias es requerida")
    @Min(value = 1, message = "La duracion debe ser al menos 1 dia")
    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "el precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener como maximo 8 digitos enteros y 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "tipoMembresia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Membresia> membresias = new ArrayList<>();

    @PrePersist
    protected void onCreate(){
        fechaCreacion = LocalDateTime.now();
    }

    //metodos de utilidad
    public String getDuracionFormateada() {
        if (duracionDias == null) {
            return "";
        }

        if (duracionDias < 30) {
            return duracionDias + " días";
        } else if (duracionDias < 365) {
            int meses = duracionDias / 30;
            int diasRestantes = duracionDias % 30;
            if (diasRestantes == 0) {
                return meses + " mes" + (meses > 1 ? "es" : "");
            } else {
                return meses + " mes" + (meses > 1 ? "es" : "") + " y " + diasRestantes + " día" + (diasRestantes > 1 ? "s" : "");
            }
        } else {
            int años = duracionDias / 365;
            int diasRestantes = duracionDias % 365;
            if (diasRestantes == 0) {
                return años + " año" + (años > 1 ? "s" : "");
            } else {
                int meses = diasRestantes / 30;
                int diasFinales = diasRestantes % 30;
                String resultado = años + " año" + (años > 1 ? "s" : "");
                if (meses > 0) {
                    resultado += " y " + meses + " mes" + (meses > 1 ? "es" : "");
                }
                if (diasFinales > 0) {
                    resultado += " y " + diasFinales + " día" + (diasFinales > 1 ? "s" : "");
                }
                return resultado;
            }
        }
    }
}
