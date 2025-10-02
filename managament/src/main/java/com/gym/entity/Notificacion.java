package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El miembro es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miembro_id", nullable = false)
    private Miembro miembro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_notificacion", nullable = false, length = 30)
    private TipoNotificacion tipoNotificacion;

    @NotBlank(message = "El titulo es requerido")
    @Size(max = 100, message = "El titulo no puede exceder 100 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "El mensaje es requerido")
    @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Builder.Default
    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enviada = false;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @PrePersist
    protected void onCreate(){
        fechaEnvio = LocalDateTime.now();
    }

    //metodos de utilidad

    public void marcarComoLeida(){
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }

    public void marcarComoEnviada(){
        this.enviada = true;
    }

    public boolean estaPendiente(){
        return !enviada && fechaProgramada != null && fechaProgramada.isAfter(LocalDateTime.now());
    }

    public enum TipoNotificacion{
        VENCIMIENTO_MEMBRESIA,
        PAGO_PENDIENTE,
        BIENVENIDA,
        GENERAL
    }
}
