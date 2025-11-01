package com.gym.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name= "configuracion_usuario")
@Data
public class ConfiguracionUsuario {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "notificaciones_email", nullable = false)
    private Boolean notificacionesEmail = true;

    @Column(name = "notificaciones_push", nullable = false)
    private String tema = "claro";

    @Column(name = "idioma", length = 10)
    private String idioma = "es";

    @Column(name="two_factor_auth")
    private Boolean twoFactorAuth = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_Actualizacion")
    private LocalDateTime fechaActualizacion;
}
