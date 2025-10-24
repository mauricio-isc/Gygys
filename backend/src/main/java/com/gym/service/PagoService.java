package com.gym.service;

import com.gym.dto.PagoDetalleResponse;
import com.gym.dto.PagoRequest;
import com.gym.entity.Membresia;
import com.gym.entity.Miembro;
import com.gym.entity.Pago;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MembresiaRepository;
import com.gym.repository.MiembroRepository;
import com.gym.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MembresiaRepository membresiaRepository;
    private final MiembroRepository miembroRepository;

    @Transactional(readOnly = true)
    public List<Pago> findAll() {
        return pagoRepository.findAllWithDetailsOrderByFechaPagoDesc();
    }

    @Transactional(readOnly = true)
    public Pago findById(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
    }

    @Transactional
    public Pago create(PagoRequest request) {
        log.info("Creando nuevo pago - Membresía: {}, Monto: {}, Método: {}",
                request.getMembresiaId(), request.getMonto(), request.getMetodoPago());

        // Validar que la membresía existe
        Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada con ID: " + request.getMembresiaId()));



        Pago pago = Pago.builder()
                .membresia(membresia)
                .monto(request.getMonto())
                .fechaPago(LocalDateTime.now()) // Fecha actual automáticamente
                .metodoPago(request.getMetodoPago())
                .referenciaPago(request.getReferenciaPago())
                .notas(request.getNotas())
                .build();

        Pago pagoGuardado = pagoRepository.save(pago);
        log.info("Pago creado exitosamente - ID: {}", pagoGuardado.getId());

        return pagoGuardado;
    }

    @Transactional
    public Pago update(Long id, PagoRequest request) {
        log.info("Actualizando pago - ID: {}", id);

        Pago pagoExistente = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));

        // Validar que la membresía existe si se está actualizando
        if (request.getMembresiaId() != null) {
            Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada con ID: " + request.getMembresiaId()));
            pagoExistente.setMembresia(membresia);
        }

        // Actualizar campos
        if (request.getMonto() != null) {
            pagoExistente.setMonto(request.getMonto());
        }
        if (request.getMetodoPago() != null) {
            pagoExistente.setMetodoPago(request.getMetodoPago());
        }
        if (request.getReferenciaPago() != null) {
            pagoExistente.setReferenciaPago(request.getReferenciaPago());
        }
        if (request.getNotas() != null) {
            pagoExistente.setNotas(request.getNotas());
        }

        Pago pagoActualizado = pagoRepository.save(pagoExistente);
        log.info("Pago actualizado exitosamente - ID: {}", pagoActualizado.getId());

        return pagoActualizado;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando pago - ID: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));

        pagoRepository.delete(pago);
        log.info("Pago eliminado exitosamente - ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<PagoDetalleResponse> obtenerPagosDetallePorMiembro(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID: " + miembroId));

        return pagoRepository.findByMembresiaMiembroOrderByFechaPagoDesc(miembro)
                .stream()
                .map(this::convertToDetalleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorMembresia(Long membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada con ID: " + membresiaId));

        return pagoRepository.findByMembresiaOrderByFechaPagoDesc(membresia);
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosRecientes(int limite) {
        return pagoRepository.findAllWithDetailsOrderByFechaPagoDesc()
                .stream()
                .limit(limite)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getIngresosDelMes() {
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return pagoRepository.sumMontoByFechaPagoAfter(firstDayOfMonth);
    }

    @Transactional(readOnly = true)
    public BigDecimal getIngresosDelAnio() {
        int currentYear = LocalDateTime.now().getYear();
        return pagoRepository.sumMontoByYear(currentYear);
    }

    @Transactional(readOnly = true)
    public long countPagosDelMes() {
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return pagoRepository.countByFechaPagoAfter(firstDayOfMonth);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getEstadisticasPorMetodoPago() {
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return pagoRepository.countAndSumByMetodoPagoAndFechaPagoAfter(firstDayOfMonth);
    }

    private PagoDetalleResponse convertToDetalleResponse(Pago pago) {
        return PagoDetalleResponse.builder()
                .id(pago.getId())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .metodoPago(pago.getMetodoPago().name())
                .referenciaPago(pago.getReferenciaPago())
                .notas(pago.getNotas())
                .nombreMiembro(pago.getMembresia().getMiembro().getNombreCompleto())
                .tipoMembresia(pago.getMembresia().getTipoMembresia().getNombre())
                .fechaInicioMembresia(pago.getMembresia().getFechaInicio())
                .fechaFinMembresia(pago.getMembresia().getFechaFin())
                .registradoPor(pago.getRegistradoPor().getUsername())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pagoRepository.findByFechaPagoAfterWithDetails(fechaInicio)
                .stream()
                .filter(pago -> pago.getFechaPago().isBefore(fechaFin))
                .collect(Collectors.toList());
    }
}