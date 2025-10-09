package com.gym.service;

import com.gym.dto.TipoMembresiaRequest;
import com.gym.dto.TipoMembresiaResponse;
import com.gym.entity.TipoMembresia;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.TipoMembresiaRepository;
import com.gym.service.mapper.TipoMembresiaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoMembresiaService {

    private final TipoMembresiaRepository tipoMembresiaRepository;
    private final TipoMembresiaMapper tipoMembresiaMapper;

    @Transactional(readOnly = true)
    public List<TipoMembresiaResponse> findAll() {
        return tipoMembresiaMapper.toResponseList(tipoMembresiaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TipoMembresiaResponse findById(Long id) {
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado con ID: " + id));
        return tipoMembresiaMapper.toResponse(tipoMembresia);
    }

    @Transactional(readOnly = true)
    public List<TipoMembresiaResponse> findByActivoTrue() {
        return tipoMembresiaMapper.toResponseList(tipoMembresiaRepository.findByActivoTrue());
    }

    @Transactional
    public TipoMembresiaResponse save(TipoMembresiaRequest request) {
        TipoMembresia tipoMembresia = tipoMembresiaMapper.toEntity(request);
        TipoMembresia saved = tipoMembresiaRepository.save(tipoMembresia);
        return tipoMembresiaMapper.toResponse(saved);
    }

    @Transactional
    public TipoMembresiaResponse update(Long id, TipoMembresiaRequest request) {
        TipoMembresia tipoExistente = tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado con ID: " + id));

        tipoExistente.setNombre(request.getNombre());
        tipoExistente.setDescripcion(request.getDescripcion());
        tipoExistente.setDuracionDias(request.getDuracionDias());
        tipoExistente.setPrecio(request.getPrecio());
        if (request.getActivo() != null) {
            tipoExistente.setActivo(request.getActivo());
        }

        TipoMembresia updated = tipoMembresiaRepository.save(tipoExistente);
        return tipoMembresiaMapper.toResponse(updated);
    }

    @Transactional
    public void deleteById(Long id) {
        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado con ID: " + id));
        tipoMembresia.setActivo(false); // Eliminación lógica
        tipoMembresiaRepository.save(tipoMembresia);
    }

    // Método para usar con la entidad directamente (si lo necesitas)
    @Transactional
    public TipoMembresia saveEntity(TipoMembresia tipoMembresia) {
        return tipoMembresiaRepository.save(tipoMembresia);
    }
}