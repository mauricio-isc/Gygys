package com.gym.service;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.exception.BusinessException;
import com.gym.repository.MembresiaRepository;
import com.gym.repository.MiembroRepository;
import com.gym.repository.TipoMembresiaRepository;
import com.gym.repository.PagoRepository;
import com.gym.repository.ConfiguracionSistemaRepository;
import com.gym.service.mapper.MembresiaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembresiaService {

        private final MembresiaRepository membresiaRepository;
        private final MiembroRepository miembroRepository;
        private final TipoMembresiaRepository tipoMembresiaRepository;
        private final PagoRepository pagoRepository;
        private final ConfiguracionSistemaRepository configuracionRepository;
        private final UsuarioService usuarioService;
        private final MembresiaMapper membresiaMapper;
        private final NotificacionService notificacionService;

        @Transactional(readOnly = true)
        public List<MembresiaResponse> findAll() {
                return membresiaRepository.findByEstadoWithDetails(Membresia.EstadoMembresia.ACTIVA)
                                .stream()
                                .map(membresiaMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public MembresiaResponse findById(Long id) {
                Membresia membresia = membresiaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Membresía no encontrada con ID: " + id));
                return membresiaMapper.toDto(membresia);
        }

        @Transactional
        public MembresiaResponse create(MembresiaRequest request) {
                log.info("Creando membresía personalizada - Miembro: {}, Tipo: {}, Fecha Inicio: {}",
                                request.getMiembroId(), request.getTipoMembresiaId(), request.getFechaInicio());

                // Validar que el miembro existe
                Miembro miembro = miembroRepository.findById(request.getMiembroId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Miembro no encontrado con ID: " + request.getMiembroId()));

                // Validar que el tipo de membresía existe
                TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tipo de membresía no encontrado con ID: "
                                                                + request.getTipoMembresiaId()));

                // Validar que el miembro no tenga una membresía activa
                if (miembro.tieneMembresiaActiva()) {
                        throw new BusinessException(
                                        "El miembro " + miembro.getNombreCompleto() + " ya tiene una membresía activa");
                }

                // Obtener el usuario actual (administrador que crea la membresía)
                Usuario creadoPor = usuarioService.findById(1L); // Por ahora usamos ID 1, en producción se obtendría
                                                                 // del contexto de seguridad

                // Calcular la fecha de fin basada en la fecha de inicio y duración del tipo de
                // membresía
                LocalDate fechaFin = request.getFechaInicio().plusDays(tipoMembresia.getDuracionDias());

                Membresia membresia = Membresia.builder()
                                .miembro(miembro)
                                .tipoMembresia(tipoMembresia)
                                .fechaInicio(request.getFechaInicio())
                                .fechaFin(fechaFin)
                                .precioPagado(request.getPrecioPagado())
                                .creadoPor(creadoPor)
                                .build();

                membresia = membresiaRepository.save(membresia);
                log.info("Membresía creada exitosamente - ID: {}", membresia.getId());

                // registrar pago automaticamente
                Pago pagoRegistrado = registrarPagoAutomatico(membresia, request.getPrecioPagado(),
                                Pago.Metodopago.EFECTIVO, null, request.getNotas(), creadoPor);
                log.info("Pago registrado automáticamente - ID: {}, Monto: {}", pagoRegistrado.getId(),
                                pagoRegistrado.getMonto());

                // Crear notificación de bienvenida
                notificacionService.crearNotificacionesBienvenida(miembro, membresia);

                return membresiaMapper.toDto(membresia);
        }

        @Transactional
        public MembresiaResponse activateMembership(Long miembroId, Long tipoMembresiaId, BigDecimal precioPagado) {
                log.info("Activando membresía - Miembro: {}, Tipo: {}, Precio: {}",
                                miembroId, tipoMembresiaId, precioPagado);

                // Validar que el miembro existe
                Miembro miembro = miembroRepository.findById(miembroId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Miembro no encontrado con ID: " + miembroId));

                // Validar que el tipo de membresía existe
                TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(tipoMembresiaId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tipo de membresía no encontrado con ID: " + tipoMembresiaId));

                // Validar que el miembro no tenga una membresía activa
                if (miembro.tieneMembresiaActiva()) {
                        throw new BusinessException(
                                        "El miembro " + miembro.getNombreCompleto() + " ya tiene una membresía activa");
                }

                // Obtener el usuario actual
                Usuario creadoPor = usuarioService.findById(1L);

                // Calcular fechas
                LocalDate fechaInicio = LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusDays(tipoMembresia.getDuracionDias()).minusDays(1);

                Membresia membresia = Membresia.builder()
                                .miembro(miembro)
                                .tipoMembresia(tipoMembresia)
                                .fechaInicio(fechaInicio)
                                .fechaFin(fechaFin)
                                .precioPagado(precioPagado)
                                .creadoPor(creadoPor)
                                .build();

                membresia = membresiaRepository.save(membresia);
                log.info("Membresía activada exitosamente - ID: {}", membresia.getId());

                // registrar pago automaticamente
                Pago pagoRegistrado = registrarPagoAutomatico(membresia, precioPagado,
                                Pago.Metodopago.EFECTIVO, null, "Pago por activación de membresía", creadoPor);
                log.info("Pago registrado automáticamente - ID: {}, Monto: {}", pagoRegistrado.getId(),
                                pagoRegistrado.getMonto());

                // Crear notificación de bienvenida
                notificacionService.crearNotificacionesBienvenida(miembro, membresia);

                return membresiaMapper.toDto(membresia);
        }

        /**
         * metodo para registrar el pago automáticamente cuando se crea una membresía
         */
        private Pago registrarPagoAutomatico(Membresia membresia, BigDecimal monto,
                        Pago.Metodopago metodoPago, String referenciaPago,
                        String notas, Usuario registradoPor) {

                // Generar referencia automática si es null o vacía
                String referenciaFinal = (referenciaPago != null && !referenciaPago.trim().isEmpty()) ? referenciaPago
                                : "REF-" + System.currentTimeMillis() + "-" + membresia.getId();

                // Generar notas automáticas si son null o vacías
                String notasFinal = (notas != null && !notas.trim().isEmpty()) ? notas
                                : String.format("Pago %s - %s - Vence: %s",
                                                metodoPago.name(),
                                                membresia.getTipoMembresia().getNombre(),
                                                membresia.getFechaFin());

                // Log para debug
                log.info("Creando pago - Referencia: {}, Notas: {}, Método: {}",
                                referenciaFinal, notasFinal, metodoPago);

                Pago pago = Pago.builder()
                                .membresia(membresia)
                                .monto(monto)
                                .metodoPago(metodoPago)
                                .referenciaPago(referenciaFinal)
                                .notas(notasFinal)
                                .registradoPor(registradoPor)
                                .fechaPago(LocalDateTime.now())
                                .build();

                return pagoRepository.save(pago);
        }

        /**
         * metodo para registrar pagos adicionales (renovaciones, pagos parciales, etc.)
         */
        @Transactional
        public Pago registrarPagoAdicional(Long membresiaId, BigDecimal monto, Pago.Metodopago metodoPago,
                        String referenciaPago, String notas) {

                log.info("Registrando pago adicional - Membresía: {}, Monto: {}, Método: {}",
                                membresiaId, monto, metodoPago);

                Membresia membresia = membresiaRepository.findById(membresiaId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Membresía no encontrada con ID: " + membresiaId));

                Usuario registradoPor = usuarioService.findById(1L);

                Pago pago = Pago.builder()
                                .membresia(membresia)
                                .monto(monto)
                                .metodoPago(metodoPago)
                                .referenciaPago(referenciaPago)
                                .notas(notas)
                                .registradoPor(registradoPor)
                                .build();

                Pago pagoGuardado = pagoRepository.save(pago);
                log.info("Pago adicional registrado exitosamente - ID: {}", pagoGuardado.getId());

                return pagoGuardado;
        }

        /**
         * Obtener todos los pagos de una membresia
         */
        @Transactional(readOnly = true)
        public List<Pago> obtenerPagosPorMembresia(Long membresiaId) {
                Membresia membresia = membresiaRepository.findById(membresiaId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Membresía no encontrada con ID: " + membresiaId));

                return pagoRepository.findByMembresiaOrderByFechaPagoDesc(membresia);
        }

        /**
         * Obtener el historial de pagos de un miembro
         */
        @Transactional(readOnly = true)
        public List<Pago> obtenerHistorialPagosMiembro(Long miembroId) {
                Miembro miembro = miembroRepository.findById(miembroId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Miembro no encontrado con ID: " + miembroId));

                return pagoRepository.findByMembresiaMiembroOrderByFechaPagoDesc(miembro);
        }

        @Transactional(readOnly = true)
        public List<MembresiaResponse> findExpiringMemberships() {
                // Obtener configuración de días de notificación
                Integer diasNotificacion = configuracionRepository.findByClave("DIAS_NOTIFICACION_VENCIMIENTO")
                                .map(config -> Integer.parseInt(config.getValor()))
                                .orElse(7);

                LocalDate fechaLimite = LocalDate.now().plusDays(diasNotificacion);
                LocalDate fechaInicio = LocalDate.now();

                return membresiaRepository.findProximasAVencerWithDetails(fechaInicio, fechaLimite)
                                .stream()
                                .map(membresiaMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public long countActiveMemberships() {
                return membresiaRepository.countByEstado(Membresia.EstadoMembresia.ACTIVA);
        }

        @Transactional(readOnly = true)
        public long countExpiredMemberships() {
                return membresiaRepository.countVencidas(LocalDate.now());
        }

        @Transactional(readOnly = true)
        public long countExpiringMemberships() {
                Integer diasNotificacion = configuracionRepository.findByClave("DIAS_NOTIFICACION_VENCIMIENTO")
                                .map(config -> Integer.parseInt(config.getValor()))
                                .orElse(7);

                LocalDate fechaInicio = LocalDate.now();
                LocalDate fechaFin = LocalDate.now().plusDays(diasNotificacion);

                return membresiaRepository.countProximasAVencer(fechaInicio, fechaFin);
        }

    @Transactional
    public void updateMembershipStatus() {
        LocalDate hoy = LocalDate.now();
        log.info("=== INICIANDO ACTUALIZACIÓN DE MEMBRESÍAS ===");
        log.info("Fecha de referencia: {}", hoy);

        try {
            List<Membresia> membresiasVencidas = membresiaRepository.findMembresiasVencidas(hoy);

            log.info("Membresías vencidas encontradas: {}", membresiasVencidas.size());

            for (Membresia membresia : membresiasVencidas) {
                log.info("Procesando ID: {} - Estado: {} - Fecha fin: {}",
                        membresia.getId(), membresia.getEstado(), membresia.getFechaFin());

                // Actualizar estado
                membresia.setEstado(Membresia.EstadoMembresia.VENCIDA);
                membresiaRepository.save(membresia);

                log.info("✅ Membresía ID: {} actualizada a VENCIDA", membresia.getId(), Membresia.EstadoMembresia.VENCIDA);
            }

            log.info("=== ACTUALIZACIÓN COMPLETADA ===");
            log.info("Total de membresías actualizadas: {}", membresiasVencidas.size());

        } catch (Exception e) {
            log.error("❌ Error en actualización de membresías: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Membresia findEntityById(Long id) {
            return membresiaRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                            "Membresía no encontrada con ID: " + id));
    }

}