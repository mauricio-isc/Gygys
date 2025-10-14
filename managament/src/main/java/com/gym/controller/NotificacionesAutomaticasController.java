package com.gym.controller;

import com.gym.service.NotificacionAutomaticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionesAutomaticasController {

    private final NotificacionAutomaticaService notificacionAutomaticaService;

    @PostMapping("/ejecutar-automaticas")
    public ResponseEntity<String> ejecutarNotificacionesAutomaticas(){
        String resultado = notificacionAutomaticaService.ejecutarNotificacionesManual();
        return ResponseEntity.ok(resultado);
    }
}
