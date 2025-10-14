package com.gym.service;

import com.gym.dto.DashboardStatsResponse;
import com.gym.repository.ConfiguracionSistemaRepository;
import com.gym.repository.MembresiaRepository;
import com.gym.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MiembroService miembroService;
    private final MembresiaService membresiaService;
    private final NotificacionService notificacionService;
    private final PagoRepository pagoRepository;
    private final ConfiguracionSistemaRepository configuracionRepository;
    private final MembresiaRepository membresiaRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(){
        DashboardStatsResponse response = new DashboardStatsResponse();

        //Estadistica de miembros
        response.setTotalMiembros(miembroService.countActiveMembers());
        response.setMiembrosActivos(miembroService.countActiveMembers());
        response.setMiembrosNuevosMes(miembroService.countNewMembersThisMonth());

        //Estadistica de membresias
        response.setMembresiasActivas(membresiaService.countActiveMemberships());
        response.setMembresiasVencidas(membresiaService.countExpiredMemberships());
        response.setMembresiasPorVencer(membresiaService.countExpiringMemberships());

        //obtener ingresos - AQUÍ ESTÁ EL PROBLEMA
        BigDecimal ingresosMes = getIngresosMesActual();
        BigDecimal ingresosAnio = getIngresosAnioActual();
        response.setIngresosMes(ingresosMes);
        response.setIngresosAnio(ingresosAnio);


        response.setIngresosMes(getIngresosMesActual());

        //Obtener notificaciones
        response.setNotificacionesPendientes(notificacionService.countUnRead());
        response.setNotificacionesLeidas(notificacionService.countRead());

        //obtener los ultimos 5 miembros registrados
        response.setUltimosMiembrosRegistrados(miembroService.getRecentMembers(5));

        //obtener membresias proximas a vencer
        response.setMembresiasProximasAVencer(membresiaService.findExpiringMemberships());

        //obtener ultimas notificaciones
        response.setUltimasNotificaciones(notificacionService.findAll()
                .stream()
                .limit(5)
                .collect(Collectors.toList()));

        return response;
    }

    private BigDecimal getIngresosMesActual(){
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return pagoRepository.sumMontoByFechaPagoAfter(firstDayOfMonth);
    }

    private BigDecimal getIngresosAnioActual(){
        int currentYear = LocalDateTime.now().getYear();
        return pagoRepository.sumMontoByYear(currentYear);
    }



    @Transactional
    public void actualizarEstadosSistema(){
        membresiaService.updateMembershipStatus();

        notificacionService.enviarNotificacionesPendientes();
    }

    private List<DashboardStatsResponse.IngresoMensual> getIngresosPorMes() {
        return IntStream.range(0, 6)
                .mapToObj(i -> {
                    LocalDateTime date = LocalDateTime.now().minusMonths(i);

                    BigDecimal ingreso = pagoRepository.sumMontoByMonthAndYear(
                            date.getMonthValue(), date.getYear()
                    );
                    long cantidadMembresias = membresiaRepository.countByFechaCreacionAfter(
                            date.withDayOfMonth(1).toLocalDate()
                    );

                    DashboardStatsResponse.IngresoMensual ingresoMensual = new DashboardStatsResponse.IngresoMensual();
                    ingresoMensual.setMes(date.format(DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es"))));
                    ingresoMensual.setIngreso(ingreso != null ? ingreso : BigDecimal.ZERO);
                    ingresoMensual.setCantidadMembresias(cantidadMembresias);

                    return ingresoMensual;
                })
                // Orden cronológico del más antiguo al más reciente
                .sorted(Comparator.comparing(im -> {
                    String[] parts = im.getMes().split(" ");
                    Month month = Month.valueOf(parts[0].toUpperCase());
                    int year = Integer.parseInt(parts[1]);
                    return year * 100 + month.getValue();
                }))
                .collect(Collectors.toList());
    }
}
