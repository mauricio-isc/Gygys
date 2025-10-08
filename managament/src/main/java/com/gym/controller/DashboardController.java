package com.gym.controller;

import com.gym.dto.DashboardStatsResponse;
import com.gym.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Endpoint para obtener todas las estadísticas del dashboard
    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        return dashboardService.getDashboardStats();
    }
}
