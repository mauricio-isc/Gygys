package com.gym.service;

import com.gym.dto.NotificacionResponse;
import com.gym.entity.Membresia;
import com.gym.entity.Miembro;
import com.gym.entity.Notificacion;
import com.gym.repository.NotificacionRepository;
import com.gym.service.mapper.NotificacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    @Transactional(readOnly = true)
    public List<NotificacionResponse> findAll(){
        return notificacionRepository.findAllWithMiembroOrderByFechaEnvioDesc()
                .stream()
                .map(notificacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> findByMiembro(Long miembroId){
        return notificacionRepository.findByMiembroOrderByFechaEnvioDesc(null) //pasar el objeto Miembro
                .stream()
                .map(notificacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> findUnread(){
        return notificacionRepository.findByLeida(false)
                .stream()
                .map(notificacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificacionResponse markAsRead(Long id){
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacion no encontrada"+id));

        notificacion.marcarComoLeida();
        notificacion = notificacionRepository.save(notificacion);

        return notificacionMapper.toDto(notificacion);
    }

    @Transactional
    public void crearNotificacionVencimiento(Miembro miembro, Membresia membresia){
        Notificacion notificacion = Notificacion.builder()
                .miembro(miembro)
                .tipoNotificacion(Notificacion.TipoNotificacion.VENCIMIENTO_MEMBRESIA)
                .titulo("Membresia vencida - "+ miembro.getNombreCompleto())
                .mensaje(String.format("Estimad@ %s, su membresia ha vencido el %s. Por favor renueve para continuar disfrutando de nuestros servicios.", miembro.getNombreCompleto(), membresia.getFechaFin()))
                .build();

        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void crearNotificacionVencimientoProximo(Miembro miembro, Membresia membresia, int diasRestantes){
        Notificacion notificacion = Notificacion.builder()
                .miembro(miembro)
                .tipoNotificacion(Notificacion.TipoNotificacion.VENCIMIENTO_MEMBRESIA)
                .titulo("Membresia proxima a vencer - "+ miembro.getNombreCompleto())
                .mensaje(String.format("Estimad@ %s, su membresia vencera en %d dias (%s). Por favor renueve a tiempo para evitar interrupciones en el servicio.",
                        miembro.getNombreCompleto(), diasRestantes, membresia.getFechaFin()))
                .fechaProgramada(LocalDateTime.now().plusHours(1))
                .build();

        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void crearNotificacionesBienvenida(Miembro miembro, Membresia membresia){
        Notificacion notificacion = Notificacion.builder()
                .miembro(miembro)
                .tipoNotificacion(Notificacion.TipoNotificacion.BIENVENIDA)
                .titulo(String.format("¡Bienvenid@ %s a nuestro gimnasio!", miembro.getNombreCompleto()))
                .mensaje(String.format
                        ("¡Hola %s! Su membresia ha sido activada exitosamente. Disfrute de nuestros servicios. Su membrecia vence el %s",
                        miembro.getNombreCompleto(), membresia.getFechaFin()))
                .build();
    }

    @Transactional
    public void enviarNotificacionesPendientes(){
        List<Notificacion> notificacionsPendientes = notificacionRepository.findPendientesPorEnviar(LocalDateTime.now());

        for (Notificacion notificacion : notificacionsPendientes){
            //se implementara el envio real ya sea por whatsapp o email
            notificacion.marcarComoEnviada();
            notificacionRepository.save(notificacion);
        }
    }

    @Transactional(readOnly = true)
    public long countUnRead(){
        return notificacionRepository.countNoLeidas();
    }

    @Transactional(readOnly = true)
    public long countRead() {
        return notificacionRepository.countLeidas();
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return notificacionRepository.countNoEnviadas();
    }
}
