package com.gym.service.mapper;

import com.gym.dto.MiembroRequest;
import com.gym.dto.MiembroResponse;
import com.gym.entity.Membresia;
import com.gym.entity.Miembro;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MembresiaMapper.class})
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

    default MiembroResponse.MembresiaResponse mapMembresiaActiva(Miembro miembro) {
        Membresia membresiaActiva = miembro.getMembresiaActiva();
        if (membresiaActiva == null) {
            return null;
        }

        MiembroResponse.MembresiaResponse response = new MiembroResponse.MembresiaResponse();
        response.setId(membresiaActiva.getId());
        response.setFechaInicio(membresiaActiva.getFechaInicio());
        response.setFechaFin(membresiaActiva.getFechaFin());
        response.setEstado(membresiaActiva.getEstado().name());
        response.setDiasRestantes(membresiaActiva.getDiasRestantes());
        response.setVencePronto(membresiaActiva.vencePronto(7));

        if (membresiaActiva.getTipoMembresia() == null) {
            MiembroResponse.TipoMembresiaResponse tipoResponse = new MiembroResponse.TipoMembresiaResponse();

            tipoResponse.setId(membresiaActiva.getTipoMembresia().getId());
            tipoResponse.setNombre(membresiaActiva.getTipoMembresia().getNombre());
            tipoResponse.setDescripcion(membresiaActiva.getTipoMembresia().getDescripcion());
            tipoResponse.setDuracionDias(membresiaActiva.getTipoMembresia().getDuracionDias());
            tipoResponse.setDuracionFormateada(membresiaActiva.getTipoMembresia().getDuracionFormateada());
            tipoResponse.setPrecio(membresiaActiva.getTipoMembresia().getPrecio());
            response.setTipoMembresia(tipoResponse);
        }
        return response;
    }
}
