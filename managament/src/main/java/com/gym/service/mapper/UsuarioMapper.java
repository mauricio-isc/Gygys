package com.gym.service.mapper;

import com.gym.dto.AuthResponse;
import com.gym.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "username", source = "entity.username")
    @Mapping(target = "email", source = "entity.email")
    @Mapping(target = "nombreCompleto", expression = "java(entity.getNombreCompleto())")
    @Mapping(target = "token", source = "token")
    AuthResponse toAuthResponse(Usuario entity, String token);
}