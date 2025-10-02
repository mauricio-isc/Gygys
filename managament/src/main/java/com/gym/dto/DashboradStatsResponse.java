package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboradStatsResponse {
    private Long totalmiembros;
    private Long miembrosActivos;
    private Long miembrosNuevosMes;
    private Long membresiasActivas;
    private Long membresiasVencidas;
    private Long membresiasPorVencer;
    private BigDecimal ingresosMes;
    private BigDecimal ingresosAnio;
    private Long notificacionesPendientes;
    private Long notificacionesLeidas;
    private List<MiembroResponse> ultimosMiembrosRegistrados;
    private List<MembresiaResponse> membresiasProximasAVencer;
    private List<NotificacionResponse> ultimasNotificaciones;
    private List<IngresoMensual> ingresosPorMes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngresoMensual {
        private String mes;
        private BigDecimal ingreso;
        private Long cantidadMembresias;
    }
}
