package com.gym.service;

import com.gym.entity.Membresia;
import com.gym.entity.Miembro;
import com.gym.entity.Notificacion;
import com.gym.repository.ConfiguracionSistemaRepository;
import com.gym.repository.MembresiaRepository;
import com.gym.repository.MiembroRepository;
import com.gym.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionAutomaticaService {

    private final MembresiaRepository membresiaRepository;
    private final MiembroRepository miembroRepository;
    private final NotificacionRepository notificacionRepository;
    private final ConfiguracionSistemaRepository configuracionSistemaRepository;
    private final NotificacionService notificacionService;

    //se ejecutara todos los dias a las 8:00am
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void generarNotificacionesAutomaticas() {
        log.info("Inicializando notificaciones automaticas...");
        try {
            //validar si las notificaciones automaticas estan activadas
            boolean notificacionesActivas = configuracionSistemaRepository.findByClave("ACTIVAR_NOTIFICACIONES")
                    .map(config -> Boolean.parseBoolean(config.getValor()))
                    .orElse(true);

            if (!notificacionesActivas) {
                log.info("notificaciones automaticas desactivadas");
                return;
            }
            //obtener dias de notificacion desde la configuracion
            int diasNotificacion = configuracionSistemaRepository.findByClave("DIAS_NOTIFICACION_VENCIMIENTO")
                    .map(config -> Integer.parseInt(config.getValor()))
                    .orElse(7);

            generarNotificacionesVencimiento(diasNotificacion);
            generarNotificacionesBienvenida();
            enviarNotificacionesPendientes();

            log.info("se han generado las notificaciones automaticas");
        } catch (Exception e) {
            log.error("Error notificaciones no generadas automaticamente", e.getMessage());
        }
    }

    private void generarNotificacionesVencimiento(int diasNotificacion) {
        LocalDate fechaLimite = LocalDate.now().plusDays(diasNotificacion);
        LocalDate fechaHoy = LocalDate.now();

        // Buscar membresias activas
        List<Membresia> membresiasProximasAVencer = membresiaRepository
                .findProximasAVencer(fechaHoy, fechaLimite);

        int notificacionesCreadas = 0;

        for (Membresia membresia : membresiasProximasAVencer) {
            if (membresia.getEstado() == Membresia.EstadoMembresia.ACTIVA) {
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(), membresia.getFechaFin()
                );

                // validar si ya existen notificaciones recientes para el vencimiento
                boolean notificacionExistente = existeNotificacionVencimientoReciente(
                        membresia.getMiembro(),
                        membresia.getFechaFin()
                );

                if (!notificacionExistente && diasRestantes > 0) {
                    notificacionService.crearNotificacionVencimientoProximo(
                            membresia.getMiembro(),
                            membresia,
                            (int) diasRestantes
                    );
                    notificacionesCreadas++;
                }
            }
        }

        log.info("Notificaciones de vencimiento generadas: {}/{}",
                notificacionesCreadas, membresiasProximasAVencer.size());
    }

    private void generarNotificacionesBienvenida() {
        //encontrar nuevos miembros en las ultimas 24horas
        LocalDateTime fechaLimite = LocalDateTime.now().minusHours(24);

        List<Miembro> miembrosNuevos = miembroRepository.findByFechaRegistroAfter(fechaLimite);

        int notificacionesCreadas = 0;
        for (Miembro miembro : miembrosNuevos) {
            //validar si el miembro tiene membresia activa
            boolean tieneMembresiaActiva = membresiaRepository
                    .findByMiembroAndEstado(miembro, Membresia.EstadoMembresia.ACTIVA)
                    .isPresent();

            if (tieneMembresiaActiva) {
                //validar si ya existe notificacion de bienvenida
                boolean notificacionExistente = notificacionRepository
                        .findByMiembroAndTipoNotificacion(miembro, Notificacion.TipoNotificacion.BIENVENIDA)
                        .stream()
                        .findFirst()
                        .isPresent();
                if (!notificacionExistente) {
                    //obbtener la membresia activa para incluir detalles
                    Membresia membresiaActiva = membresiaRepository
                            .findByMiembroAndEstado(miembro, Membresia.EstadoMembresia.ACTIVA)
                            .orElse(null);
                    if (membresiaActiva != null) {
                        notificacionService.crearNotificacionesBienvenida(miembro, membresiaActiva);
                        notificacionesCreadas++;
                    }
                }
            }
            log.info("notificaciones creadas de bienvenida: {}", notificacionesCreadas);
        }
    }

    private void enviarNotificacionesPendientes () {
        notificacionService.enviarNotificacionesPendientes();
        log.info("Notificaciones pendientes creadas");
    }

    private boolean existeNotificacionVencimientoReciente(Miembro miembro, LocalDate fechaVencimiento){

        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(1); //24 horas

        return notificacionRepository.findByMiembro(miembro)
                .stream()
                .filter(n -> n.getTipoNotificacion() == Notificacion.TipoNotificacion.VENCIMIENTO_MEMBRESIA)
                .filter(n -> n.getFechaEnvio().isAfter(fechaLimite))
                .anyMatch(n -> n.getMensaje().contains(fechaVencimiento.toString()));
    }

    //metodo para ejecucion manual desde el controlador
    @Transactional
    public String ejecutarNotificacionesManual(){
        try {
            generarNotificacionesAutomaticas();
            return "Notificaciones automaticas generadas exitosamente";
        } catch (Exception e) {
            log.error("Error en la ejecucion manual: {}", e.getMessage(), e);
            return "Error al ejecutar notificaciones" + e.getMessage();
        }
    }
}