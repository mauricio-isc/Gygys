package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_sistema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La clave es requerida")
    @Size(max = 100, message = "La clave no puede exceder 100 caracteres")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String clave;

    @NotBlank(message = "El valor es requerido")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String valor;

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato", nullable = false, length = 10)
    private TipoDato tipoDato;

    @Builder.Default
    @Column(nullable = false)
    private Boolean editable = true;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    protected void onUpdate(){
        fechaActualizacion = LocalDateTime.now();
    }


    //METODOS
    public String getValorString(){
        return valor;
    }

    public Integer getValorInteger() {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Long getValorLong(){
        try{
            return Long.parseLong(valor);
        }catch (NumberFormatException e){
            return null;
        }
    }

    public BigDecimal getValorBigDecimal(){
        try{
            return new BigDecimal(valor);
        }catch (NumberFormatException e){
            return null;
        }
    }

    public Boolean getValorBoolean(){
        return Boolean.valueOf(valor);
    }

    public enum TipoDato{
        STRING, NUMBER, BOOLEAN, JSON
    }
}
