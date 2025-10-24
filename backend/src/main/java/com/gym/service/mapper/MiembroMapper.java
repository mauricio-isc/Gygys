package com.gym.service.mapper;

import com.gym.dto.MiembroRequest;
import com.gym.dto.MiembroResponse;
import com.gym.dto.MembresiaResponse;
import com.gym.dto.TipoMembresiaResponse;
import com.gym.entity.Miembro;
import com.gym.entity.Membresia;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MiembroMapper extends EntityMapper<MiembroRequest, Miembro> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "membresias", ignore = true)
    @Mapping(target = "notificaciones", ignore = true)
    Miembro toEntity(MiembroRequest dto);

    @Mapping(target = "nombreCompleto", expression = "java(entity.getNombreCompleto())")
    @Mapping(target = "edad", expression = "java(entity.getEdad())")
    @Mapping(target = "tieneMembresiaActiva", expression = "java(entity.tieneMembresiaActiva())")
    @Mapping(target = "membresiaActiva", expression = "java(mapMembresiaActiva(entity))")
    @Mapping(target = "genero", source = "genero")
    MiembroResponse toDto(Miembro entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "membresias", ignore = true)
    @Mapping(target = "notificaciones", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(MiembroRequest dto, @MappingTarget Miembro entity);

    default MembresiaResponse mapMembresiaActiva(Miembro miembro) {
        Membresia m = miembro.getMembresiaActiva();
        if (m == null) return null;

        return MembresiaResponse.builder()
                .id(m.getId())
                .fechaInicio(m.getFechaInicio())
                .fechaFin(m.getFechaFin())
                .estado(m.getEstado().name())
                .diasRestantes(m.getDiasRestantes())
                .vencePronto(m.vencePronto(7))
                .tipoMembresia(
                        TipoMembresiaResponse.builder()
                                .id(m.getTipoMembresia().getId())
                                .nombre(m.getTipoMembresia().getNombre())
                                .descripcion(m.getTipoMembresia().getDescripcion())
                                .duracionDias(m.getTipoMembresia().getDuracionDias())
                                .duracionFormateada(m.getTipoMembresia().getDuracionFormateada())
                                .precio(m.getTipoMembresia().getPrecio())
                                .build())
                .build();
    }
}
