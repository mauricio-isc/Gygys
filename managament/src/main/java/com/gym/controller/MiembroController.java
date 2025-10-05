package com.gym.controller;

import com.gym.dto.MiembroRequest;
import com.gym.dto.MiembroResponse;
import com.gym.service.MiembroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MiembroController {

    private final MiembroService miembroService;

    @GetMapping
    public ResponseEntity<Page<MiembroResponse>> findAll(Pageable pageable){
        Page<MiembroResponse> miembros = miembroService.findAll(pageable);
        return ResponseEntity.ok(miembros);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MiembroResponse>> search(
            @RequestParam(required = false) String search, Pageable pageable){

        Page<MiembroResponse> miembros = miembroService.search(search, pageable);
        return ResponseEntity.ok(miembros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MiembroResponse> findById(@PathVariable Long id){
        MiembroResponse miembro = miembroService.findById(id);
        return ResponseEntity.ok(miembro);
    }

    @PostMapping
    public  ResponseEntity<MiembroResponse> create(@Valid @RequestBody MiembroRequest request){
        MiembroResponse miembro = miembroService.create(request);
        return new ResponseEntity<>(miembro, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MiembroResponse> update(@PathVariable Long id, @Valid @RequestBody MiembroRequest request){
        MiembroResponse miembro = miembroService.update(id, request);
        return ResponseEntity.ok(miembro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        miembroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<MiembroResponse>> findAllActive(){
        List<MiembroResponse> miembros = miembroService.findAllActive();
        return  ResponseEntity.ok(miembros);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MiembroResponse>> getRecentMembers(){
        List<MiembroResponse> miembros = miembroService.getRecentMembers(5);
        return ResponseEntity.ok(miembros);
    }
}
