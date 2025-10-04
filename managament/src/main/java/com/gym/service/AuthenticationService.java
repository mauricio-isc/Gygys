package com.gym.service;

import com.gym.dto.AuthRequest;
import com.gym.dto.AuthResponse;
import com.gym.entity.Usuario;
import com.gym.repository.UsuarioRepository;
import com.gym.security.JwtService;
import com.gym.service.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    public AuthResponse authenticate(AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        //Actualizar ultimo accesso y reestablecer intentos fallidos
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuario.resetearIntentosFallidos();
        usuarioRepository.save(usuario);

        String jwtToken = jwtService.generateToken(usuario);

        return usuarioMapper.toAuthResponse(usuario, jwtToken);
    }

    public void logout(String username){
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

}
