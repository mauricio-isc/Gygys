package com.gym.service;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.exception.BusinessException;
import com.gym.repository.MembresiaRepository;
import com.gym.repository.MiembroRepository;
import com.gym.repository.TipoMembresiaRepository;
import com.gym.repository.ConfiguracionSistemaRepository;
import com.gym.service.mapper.MembresiaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final MiembroRepository miembroRepository;
    private final TipoMembresiaRepository tipoMembresiaRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada con ID: " + id));
        return membresiaMapper.toDto(membresia);
    }

    @Transactional
    public MembresiaResponse create(MembresiaRequest request) {
        // Validar que el miembro existe
        Miembro miembro = miembroRepository.findById(request.getMiembroId())
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID: " + request.getMiembroId()));

        // Validar que el tipo de membresía existe
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado con ID: " + request.getTipoMembresiaId()));

        // Validar que el miembro no tenga una membresía activa
        if (miembro.tieneMembresiaActiva()) {
            throw new BusinessException("El miembro ya tiene una membresía activa");
        }

        // Obtener el usuario actual (administrador que crea la membresía)
        Usuario creadoPor = usuarioService.findById(1L); // Por ahora usamos ID 1, en producción se obtendría del contexto de seguridad

        // Calcular la fecha de fin basada en la fecha de inicio y duración del tipo de membresía
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

        // Crear notificación de bienvenida
        notificacionService.crearNotificacionesBienvenida(miembro, membresia);

        return membresiaMapper.toDto(membresia);
    }

    @Transactional
    public MembresiaResponse activateMembership(Long miembroId, Long tipoMembresiaId, BigDecimal precioPagado) {
        // Validar que el miembro existe
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID: " + miembroId));

        // Validar que el tipo de membresía existe
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(tipoMembresiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado con ID: " + tipoMembresiaId));

        // Validar que el miembro no tenga una membresía activa
        if (miembro.tieneMembresiaActiva()) {
            throw new BusinessException("El miembro ya tiene una membresía activa");
        }

        // Obtener el usuario actual
        Usuario creadoPor = usuarioService.findById(1L);

        // Calcular fechas
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(tipoMembresia.getDuracionDias());

        Membresia membresia = Membresia.builder()
                .miembro(miembro)
                .tipoMembresia(tipoMembresia)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .precioPagado(precioPagado)
                .creadoPor(creadoPor)
                .build();

        membresia = membresiaRepository.save(membresia);

        // Crear notificación de bienvenida
        notificacionService.crearNotificacionesBienvenida(miembro, membresia);

        return membresiaMapper.toDto(membresia);
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
        // Actualizar membresías vencidas
        List<Membresia> vencidas = membresiaRepository.findVencidas(LocalDate.now());

        for (Membresia membresia : vencidas) {
            if (membresia.getEstado() == Membresia.EstadoMembresia.ACTIVA) {
                membresia.setEstado(Membresia.EstadoMembresia.VENCIDA);
                membresiaRepository.save(membresia);

                // Crear notificación de vencimiento
                notificacionService.crearNotificacionVencimiento(membresia.getMiembro(), membresia);
            }
        }
    }

    @Transactional(readOnly = true)
    public Membresia findEntityById(Long id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada con ID: " + id));
    }
}