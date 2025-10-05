package com.gym.repository;

import com.gym.entity.Membresia;
import com.gym.entity.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByMiembro(Miembro miembro);

    Optional<Membresia> findByMiembroAndEstado(Miembro miembro, Membresia.EstadoMembresia estado);

    List<Membresia> findByEstado(Membresia.EstadoMembresia estado);

    @Query("SELECT m FROM Membresia m WHERE m.estado = 'ACTIVA' AND m.fechaFin <:fecha")
    List<Membresia> findVencidas(@Param("fecha")LocalDate fecha);

    @Query("SELECT m FROM Membresia m WHERE m.estado = 'ACTIVA' AND m.fechaFin BETWEEN :fechaInicio AND :fechaFin")
    List<Membresia> findProximasAVencer(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = :estado")
    long countByEstado(@Param("estado") Membresia.EstadoMembresia estado);

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = 'ACTIVA' AND m.fechaFin < :fecha")
    long countVencidas(@Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = 'ACTIVA' AND m.fechaFin BETWEEN :fechaInicio AND :fechaFin")
    long countProximasAVencer(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT m FROM Membresia m" +
        " JOIN FETCH m.miembro mi"+
        " JOIN FETCH m.tipoMembresia tm"+
        " WHERE m.estado = 'ACTIVA' AND m.fechaFin BETWEEN :fechaInicio AND :fechaFin"+
        " ORDER BY m.fechaFin ASC")
    List<Membresia> findProximasAVencerWithDetails(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT m FROM Membresia m "+
            " JOIN FETCH m.miembro mi "+
            " JOIN FETCH m.tipoMembresia tm "+
            " WHERE m.estado = :estado"
    )
    List<Membresia> findByEstadoWithDetails(@Param("estado") Membresia.EstadoMembresia estado);

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.fechaCreacion >= :fechaInicio")
    long countByFechaCreacionAfter(@Param("fechaInicio") LocalDate fechaInicio);

}
