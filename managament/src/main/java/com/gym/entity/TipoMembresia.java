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

    private static final int DIAS_POR_MES = 30;
    private static final int DIAS_POR_ANIO = 365;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    //metodos de utilidad
    //refactorizar ***
    public String getDuracionFormateada() {
        if(duracionDias == null)return null;

        if (duracionDias < DIAS_POR_MES){
            return formatearDias(duracionDias);
        } else if (duracionDias < DIAS_POR_ANIO) {
            return formatearMeses(duracionDias);
        }else {
            return formatearAnios(duracionDias);
        }
    }

    private String formatearDias(int dias){
        return dias + " día" + (dias > 1 ? "s" : "");
    }

    private String formatearMeses(int dias){
        int meses = dias / DIAS_POR_MES;
        int diasRestantes = dias % DIAS_POR_MES;

        if (diasRestantes == 0){
            return meses + " mes" + (meses > 1 ? "es" : "");
        }else{
            return meses + " mes" + (meses > 1 ? "es" : "") + " y " + formatearDias(diasRestantes);
        }
    }

    private  String formatearAnios(int dias){
        int anios = dias / DIAS_POR_ANIO;
        int diasRestantes = dias % DIAS_POR_ANIO;

        StringBuilder resultado = new StringBuilder();
        resultado.append(anios).append(" año").append(anios > 1 ? "s": "");

        if (diasRestantes > 0){
            int diasFinales = diasRestantes % DIAS_POR_MES;
            int meses = diasRestantes / DIAS_POR_MES;
            if (meses > 0 ){
                resultado.append(" y ").append(meses).append(" mes").append(meses > 1 ? "es" : "");
            }
            if (diasFinales > 0){
                resultado.append(" y ").append(formatearDias(diasFinales));
            }
        }
        return resultado.toString();
    }

}
