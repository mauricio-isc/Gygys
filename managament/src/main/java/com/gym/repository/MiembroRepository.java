package com.gym.repository;

import com.gym.entity.Miembro;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    Optional<Miembro> findByEmail(String email);

    Optional<Miembro> findByDocumentoIdentidad(String documentoIdentidad);

    boolean existsByEmail(String email);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    @Query("SELECT m FROM Miembro m WHERE m.activo = true")
    Page<Miembro> findByActivoTrue(Pageable pageable);

    @Query("SELECT m FROM Miembro m WHERE m.activo = true")
    List<Miembro> findByActivoTrue();

    @Query("SELECT m FROM Miembro m WHERE m.activo = true AND (" +
       "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
       "LOWER(m.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
       "LOWER(m.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
       "LOWER(m.documentoIdentidad) LIKE LOWER(CONCAT('%', :search, '%'))" +
       ")")
    Page<Miembro> searchActiveMembers(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Miembro m WHERE m.activo = true")
    long countByActivoTrue();

    @Query("SELECT COUNT(m) FROM Miembro m WHERE m.fechaRegistro >= :fechaInicio")
    long countByFechaRegistroAfter(@Param("fechaInicio")LocalDateTime fechaInicio);

    @Query("SELECT m FROM Miembro m WHERE m.activo = true ORDER BY m.fechaRegistro DESC")
    List<Miembro> findTop5ByOrderByFechaRegistroDesc(Pageable pageable);

    @Query("SELECT m FROM Miembro m"+
            " LEFT JOIN FETCH m.membresias mem"+
            " WHERE m.id = :id")
    Optional<Miembro> findByIdWithMembresias(@Param("id") Long id);

    List<Miembro> findByFechaRegistroAfter(LocalDateTime fecha);

    @Query("SELECT m FROM Miembro m WHERE m.fechaRegistro >= :fecha AND m.activo = true")
    List<Miembro> findMiembrosNuevosActivos(@Param("fecha") LocalDateTime fecha);

}
