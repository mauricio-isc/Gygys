package com.gym.scheduler;

import com.gym.service.MembresiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembresiaScheduler {

    private final MembresiaService membresiaService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Mexico_City")
    public void actualizarMembresias(){
        try {
            System.out.println("Ejecutando actualizacion automatica de membresias...");
            membresiaService.updateMembershipStatus();
        }catch (Exception e){
            System.out.println("Estados de membresia actualizados.");
        }
    }
}
