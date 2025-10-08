package com.gym.config;

import com.gym.entity.Usuario;
import com.gym.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initUsers(UsuarioRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@gym.com");
                admin.setActivo(true);
                admin.setNombre("Administrador");
                admin.setApellido("Principal");
                admin.setBloqueado(false);

                userRepository.save(admin);
                System.out.println("✅ Usuario admin creado con contraseña 'admin123'");
            } else {
                System.out.println("ℹ️ Usuario admin ya existe, no se crea nuevamente.");
            }
        };
    }
}
