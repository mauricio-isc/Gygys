package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "miembros")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Miembro {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^[0-9\\+\\-\\s\\(\\)]+$", message = "El formato del teléfono no es válido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccion;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String notas;

    @NotBlank(message = "El documento de identidad es requerido")
    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    @Column(name = "documento_identidad", unique = true, nullable = false)
    private String documentoIdentidad;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Genero genero;

    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    @Column(name = "foto_url")
    private String fotoUrl;

    @OneToMany(mappedBy = "miembro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Membresia> membresias = new ArrayList<>();

    @OneToMany(mappedBy = "miembro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Notificacion> notificaciones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    public Membresia getMembresiaActiva() {
        return membresias.stream()
                .filter(m -> m.getEstado() == Membresia.EstadoMembresia.ACTIVA)
                .findFirst()
                .orElse(null);
    }

    public boolean tieneMembresiaActiva() {
        return membresias.stream()
                .anyMatch(m -> m.getEstado() == Membresia.EstadoMembresia.ACTIVA);
    }


    public enum Genero {
        MASCULINO, FEMENINO, OTRO
    }
}