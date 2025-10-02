package com.gym.dto;

import com.gym.entity.Miembro;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiembroRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String apellido;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es valido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9\\+\\-\\s\\(\\)]+$", message = "El formato del teléfono no es válido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @Size(max = 500, message = "La direccion no puede exceder 500 caracteres")
    private String direccion;

    @NotBlank(message = "El documento de identidad es requerido")
    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    private String documentoIdentidad;

    private Miembro.Genero genero;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notas;

}
