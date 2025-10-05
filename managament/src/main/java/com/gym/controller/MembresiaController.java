package com.gym.controller;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.service.MembresiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MembresiaController {

    private final MembresiaService membresiaService;

    @GetMapping
    public ResponseEntity<List<MembresiaResponse>> findAll() {
        List<MembresiaResponse> memberships = membresiaService.findAll();
        return ResponseEntity.ok(memberships);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembresiaResponse> findById(@PathVariable Long id) {
        MembresiaResponse membership = membresiaService.findById(id);
        return ResponseEntity.ok(membership);
    }

    @PostMapping
    public ResponseEntity<MembresiaResponse> create(@Valid @RequestBody MembresiaRequest request) {
        MembresiaResponse membership = membresiaService.create(request);
        return new ResponseEntity<>(membership, HttpStatus.CREATED);
    }

    @PostMapping("/activate")
    public ResponseEntity<MembresiaResponse> activateMembership(
            @RequestParam Long miembroId,
            @RequestParam Long tipoMembresiaId,
            @RequestParam BigDecimal precioPagado) {
        MembresiaResponse membership = membresiaService.activateMembership(miembroId, tipoMembresiaId, precioPagado);
        return new ResponseEntity<>(membership, HttpStatus.CREATED);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<MembresiaResponse>> findExpiringMemberships() {
        List<MembresiaResponse> memberships = membresiaService.findExpiringMemberships();
        return ResponseEntity.ok(memberships);
    }

    @GetMapping("/stats")
    public ResponseEntity<MembershipStats> getMembershipStats() {
        MembershipStats stats = new MembershipStats();
        stats.setActiveMemberships(membresiaService.countActiveMemberships());
        stats.setExpiredMemberships(membresiaService.countExpiredMemberships());
        stats.setExpiringMemberships(membresiaService.countExpiringMemberships());
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/update-status")
    public ResponseEntity<Void> updateMembershipStatus() {
        membresiaService.updateMembershipStatus();
        return ResponseEntity.ok().build();
    }

    public static class MembershipStats {
        private long activeMemberships;
        private long expiredMemberships;
        private long expiringMemberships;

        // Getters and setters
        public long getActiveMemberships() {
            return activeMemberships;
        }

        public void setActiveMemberships(long activeMemberships) {
            this.activeMemberships = activeMemberships;
        }

        public long getExpiredMemberships() {
            return expiredMemberships;
        }

        public void setExpiredMemberships(long expiredMemberships) {
            this.expiredMemberships = expiredMemberships;
        }

        public long getExpiringMemberships() {
            return expiringMemberships;
        }

        public void setExpiringMemberships(long expiringMemberships) {
            this.expiringMemberships = expiringMemberships;
        }
    }
}