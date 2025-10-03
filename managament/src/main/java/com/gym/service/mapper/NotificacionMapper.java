package com.gym.service.mapper;

import com.gym.dto.NotificacionResponse;
import com.gym.entity.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    @Mapping(target = "miembroId", source = "miembro.id")
    @Mapping(target = "nombreMiembro", expression = "java(entity.getMiembro().getNombreCompleto())")
    @Mapping(target = "tipoNotificacion", expression = "java(entity.getTipoNotificacion().name())")
    @Mapping(target = "pendiente", expression = "java(entity.estaPendiente())")
    NotificacionResponse toDto(Notificacion entity);
}
