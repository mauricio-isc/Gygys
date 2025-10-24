package com.gym.controller;

import com.gym.dto.PagoDetalleResponse;
import com.gym.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<List<PagoDetalleResponse>> getPagosPorMiembro(
            @PathVariable Long miembroId) {
        List<PagoDetalleResponse> pagos = pagoService.obtenerPagosDetallePorMiembro(miembroId);
        return ResponseEntity.ok(pagos);
    }
}
