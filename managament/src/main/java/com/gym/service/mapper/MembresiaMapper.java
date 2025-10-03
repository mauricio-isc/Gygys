package com.gym.service.mapper;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.entity.Membresia;
import org.mapstruct.*;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface MembresiaMapper extends EntityMapper<MembresiaRequest, Membresia>{

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "miembro", ignore = true)
    @Mapping(target = "tipoMembresia", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    Membresia toEntity(MembresiaRequest dto);

    @Mapping(target = "miembroId", source = "miembro.id")
    @Mapping(target = "nombreMiembro", expression = "java(entity.getMiembro().getNombreCompleto())")
    @Mapping(target = "tipoMembresiaId", source = "tipoMembresia.id")
    @Mapping(target = "nombreTipoMembresia", source = "tipoMembresia.nombre")
    @Mapping(target = "creadoPor", source = "creadoPor.id")
    @Mapping(target = "nombreCreador", expression = "java(entity.getCreadoPor().getNombreCompleto())")
    @Mapping(target = "estado", expression = "java(entity.getEstado().name())")
    @Mapping(target = "diasRestantes", expression = "java(entity.getDiasRestantes())")
    @Mapping(target = "vencida", expression = "java(entity.getVencida()")
    @Mapping(target = "vencePronto", expression = "java(entity.getVencePronto(7))")
    @Mapping(target = "diasParaVencimiento", expression = "java(entity.vencePronto(7) ? entity.getDiasRestantes() : null)")
    MembresiaResponse toDto(Membresia entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "miembro", ignore = true)
    @Mapping(target = "tipoMembresia", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFormDto(MembresiaRequest dto, @MappingTarget Membresia entity);

    default LocalDate calculateFechaFin(LocalDate fechaInicio, Integer duracionDias){
        if (fechaInicio == null || duracionDias == null){
            return null;
        }
        return fechaInicio.plusDays(duracionDias);
    }
}
