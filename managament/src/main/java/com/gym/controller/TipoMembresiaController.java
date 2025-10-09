package com.gym.controller;

import com.gym.dto.TipoMembresiaRequest;
import com.gym.dto.TipoMembresiaResponse;
import com.gym.service.TipoMembresiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/type")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TipoMembresiaController {

    private final TipoMembresiaService tipoMembresiaService;

    @GetMapping
    public ResponseEntity<List<TipoMembresiaResponse>> findAll() {
        List<TipoMembresiaResponse> tipos = tipoMembresiaService.findAll();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoMembresiaResponse> findById(@PathVariable Long id) {
        TipoMembresiaResponse tipo = tipoMembresiaService.findById(id);
        return ResponseEntity.ok(tipo);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<TipoMembresiaResponse>> findActive() {
        List<TipoMembresiaResponse> tipos = tipoMembresiaService.findByActivoTrue();
        return ResponseEntity.ok(tipos);
    }

    @PostMapping
    public ResponseEntity<TipoMembresiaResponse> create(@Valid @RequestBody TipoMembresiaRequest request) {
        TipoMembresiaResponse nuevoTipo = tipoMembresiaService.save(request);
        return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoMembresiaResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody TipoMembresiaRequest request) {
        TipoMembresiaResponse tipoActualizado = tipoMembresiaService.update(id, request);
        return ResponseEntity.ok(tipoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoMembresiaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}