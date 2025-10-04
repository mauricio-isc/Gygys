package com.gym.service.mapper;

import com.gym.dto.MiembroRequest;
import com.gym.dto.MiembroResponse;
import com.gym.entity.Miembro;
import com.gym.exception.BusinessException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MiembroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MiembroService {

    private final MiembroRepository miembroRepository;
    private final MiembroMapper miembroMapper;

    @Transactional(readOnly = true)
    public Page<MiembroResponse> findAll(Pageable pageable){
        return miembroRepository.findByActivoTrue(pageable)
                .map(miembroMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<MiembroResponse> search(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return miembroRepository.searchActiveMembers(search, pageable)
                .map(miembroMapper::toDto);
    }


    @Transactional
    public MiembroResponse create(MiembroRequest request){
        //verificar que el email no exista
        if (miembroRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Ya existe un miembro con el email: "+ request.getEmail());
        }

        //verificar que el documento de identidad no exista
        if (miembroRepository.existsByDocumentoIdentidad(request.getDocumentoIdentidad())){
            throw new BusinessException("Ya existe un miembro con el documento de identidad: " + request.getDocumentoIdentidad());
        }

        Miembro miembro = miembroMapper.toEntity(request);
        miembro = miembroRepository.save(miembro);
        return miembroMapper.toDto(miembro);
    }

    @Transactional
    public MiembroResponse update(Long id, MiembroRequest request){
        Miembro miembro = miembroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID: "+id));

        //verificar que el email no este duplicado
        if (!miembro.getEmail().equals(request.getEmail()) &&
                miembroRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe otro miembro con el email: " + request.getEmail());
        }

        //verificar que el documento de identidad no sea duplicado
        if (!miembro.getDocumentoIdentidad().equals(request.getDocumentoIdentidad()) &&
        miembroRepository.existsByDocumentoIdentidad(request.getDocumentoIdentidad())){
            throw new BusinessException("Ya existe otro miembro con el documento de identidad: "+request.getDocumentoIdentidad());
        }

        miembroMapper.updateFromDto(request, miembro);
        miembro = miembroRepository.save(miembro);
        return miembroMapper.toDto(miembro);
    }

    @Transactional
    public void delete(Long id){
        Miembro miembro = miembroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID:" + id));

        //marcar como inactivo
        miembro.setActivo(false);
        miembroRepository.save(miembro);
    }

    @Transactional(readOnly = true)
    public List<MiembroResponse> findAllActive(){
        return miembroRepository.findByActivoTrue()
                .stream()
                .map(miembroMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countActiveMembers(){
        return miembroRepository.countByActivoTrue();
    }

    @Transactional(readOnly = true)
    public long countNewMembersThisMonth(){
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return miembroRepository.countByFechaRegistroAfter(firstDayOfMonth);
    }

    @Transactional(readOnly = true)
    public List<MiembroResponse> getRecentMembers(int limit){
        return miembroRepository.findTop5ByOrderByFechaRegistroDesc(null)
                .stream()
                .map(miembroMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Miembro findEntityById(Long id){
        return miembroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Miembro no encontrado con ID" + id));
    }
}
