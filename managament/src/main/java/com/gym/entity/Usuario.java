package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El username es requerido")
    @Size(min = 3, max = 50, message = "El username debe tene entre 3 y 50 caracteres")
    @Column(nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es valido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    @Column(nullable = false)
    private String apellido;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Builder.Default
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean bloqueado = false;

    @PrePersist
    protected void onCreate(){
        fechaCreacion = LocalDateTime.now();
    }

    //implementacion de userdetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    //veremos si se pueden quitar
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    //METODOS
    public String getNombreCompleto(){
        return nombre + " " + apellido;
    }

    public void incrementarIntentosFallidos(){
        this.intentosFallidos++;
        if (this.intentosFallidos >= 3){
            this.bloqueado = true;
        }
    }

    public void resetearIntentosFallidos(){
        this.intentosFallidos = 0;
        this.bloqueado = false;
    }
}
