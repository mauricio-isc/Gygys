package com.gym.service;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.entity.Membresia;
import com.gym.entity.Miembro;
import com.gym.entity.TipoMembresia;
import com.gym.entity.Usuario;
import com.gym.exception.BusinessException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.ConfiguracionSistemaRepository;
import com.gym.repository.MembresiaRepository;
import com.gym.repository.MiembroRepository;
import com.gym.repository.TipoMembresiaRepository;
import com.gym.service.mapper.MembresiaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final MiembroRepository miembroRepository;
    private final TipoMembresiaRepository tipoMembresiaRepository;
    private final ConfiguracionSistemaRepository configuracionRepository;
    private final  UsuarioService usuarioService;
    private final MembresiaMapper membresiaMapper;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public List<MembresiaResponse> findAll(){
        return membresiaRepository.findByEstadoWithDetails(Membresia.EstadoMembresia.ACTIVA)
                .stream()
                .map(membresiaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembresiaResponse findById(Long id){
        Membresia membresia = membresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membresia no encontrada con ID: "+id));
        return membresiaMapper.toDto(membresia);
    }

    @Transactional
    public MembresiaResponse create(MembresiaRequest request){
        //verificar que existe
        Miembro miembro = miembroRepository.findById(request.getMiembroId())
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID: "+request.getMiembroId()));

        //verificar que el tipo de membresia exista
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tipo de membresia no encontrado con ID"
                        + request.getTipoMembresiaId()));

        //verificar que el miembro no tenga membresia activa
        if (miembro.tieneMembresiaActiva()){
            throw new BusinessException("El miembro ya tiene una membresia activa");
        }

        //obtener el usuario actual osea el admin que creo la membresia
        Usuario creadoPor = usuarioService.findById(1L); //por el momento el id 1 en produccion en produccion ya se cambiara

        //calcular la fecha de fin basada en la fecha de inicio y duracion del tipo de membresia
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

        //crear notificacion de bienvenida
        notificacionService.crearNotificacionesBienvenida(miembro, membresia);

        return membresiaMapper.toDto(membresia);
    }

    @Transactional
    public MembresiaResponse activateMembership(Long miembroId, Long tipoMembresiaId, BigDecimal precioPagado){
        //verificar si el miembro existe
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Miembro no encontrado con ID:"+miembroId));

        //verificar que el tipo de membresia existe
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(tipoMembresiaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tipo de membresia no encontrado con ID: "+tipoMembresiaId));

        //verificar que el miembro no tenga una membresia
        if (miembro.tieneMembresiaActiva()){
            throw new BusinessException("El miembro ya tiene una membresia activa");
        }

        //Obtener el usuario actual
        Usuario creadoPor = usuarioService.findById(1L);

        //calcular fecha
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

        //crear  notificacion de bienvenida
        notificacionService.crearNotificacionesBienvenida(miembro, membresia);

        return  membresiaMapper.toDto(membresia);
    }

    @Transactional(readOnly = true)
    public List<MembresiaResponse> findExpiringMemberships(){
        //obtener la confi de dias de notificacion
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
    public long  countActiveMembership(){
        return membresiaRepository.countByEstado(Membresia.EstadoMembresia.ACTIVA);
    }

    @Transactional(readOnly = true)
    public long countExpiredMemberships(){
        return membresiaRepository.countVencidas(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public long countExpiringMemberships(){
        Integer diasNotificacion = configuracionRepository.findByClave("DIAS_NOTIFICACION_VENCIMIENTO")
                .map(config -> Integer.parseInt(config.getValor()))
                .orElse(7);

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(diasNotificacion);

        return membresiaRepository.countProximasAVencer(fechaInicio, fechaFin);
    }

    @Transactional
    public void updateMemberhipStatus(){
        //actualizar membrecias que ya vencieron
        List<Membresia> vencidas = membresiaRepository.findVencidas(LocalDate.now());

        for (Membresia membresia : vencidas){
            if (membresia.getEstado() == Membresia.EstadoMembresia.ACTIVA){
                membresia.setEstado(Membresia.EstadoMembresia.VENCIDA);
                membresiaRepository.save(membresia);

                //crear notificaciones de vencimiento
                notificacionService.crearNotificacionVencimiento(membresia.getMiembro(), membresia);
            }
        }
    }

    @Transactional(readOnly = true)
    public Membresia findEntityById(Long id){
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membresia no encontrada con ID: " +id));
    }
}
