package com.gym.service.mapper;

import com.gym.dto.TipoMembresiaRequest;
import com.gym.dto.TipoMembresiaResponse;
import com.gym.entity.TipoMembresia;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TipoMembresiaMapper {

    public TipoMembresiaResponse toResponse(TipoMembresia tipoMembresia) {
        if (tipoMembresia == null) {
            return null;
        }

        return TipoMembresiaResponse.builder()
                .id(tipoMembresia.getId())
                .nombre(tipoMembresia.getNombre())
                .descripcion(tipoMembresia.getDescripcion())
                .duracionDias(tipoMembresia.getDuracionDias())
                .precio(tipoMembresia.getPrecio())
                .activo(tipoMembresia.getActivo())
                .fechaCreacion(tipoMembresia.getFechaCreacion())
                .build();
    }

    public List<TipoMembresiaResponse> toResponseList(List<TipoMembresia> tiposMembresia) {
        return tiposMembresia.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TipoMembresia toEntity(TipoMembresiaRequest request) {
        return TipoMembresia.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionDias(request.getDuracionDias())
                .precio(request.getPrecio())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
    }
}