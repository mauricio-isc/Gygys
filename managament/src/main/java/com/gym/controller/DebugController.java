package com.gym.controller;

import com.gym.service.MembresiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final MembresiaService membresiaService;

    @GetMapping("/test")
    public String test() {
        log.info("Debug test endpoint called");
        return "Debug controller is working! Time: " + java.time.LocalDateTime.now();
    }

    @GetMapping("/membresias-vencidas")
    public String diagnosticarVencimientos() {
        log.info("Diagnóstico de membresías vencidas llamado");
        membresiaService.diagnosticarMembresiaVencida();
        return "Diagnóstico ejecutado - Revisa los logs de la consola";
    }

    @GetMapping("/actualizar-estados")
    public String actualizarEstados() {
        log.info("Actualización de estados llamada");
        membresiaService.updateMembershipStatus();
        return "Estados de membresías actualizados";
    }
}