package com.gym.repository;

import com.gym.entity.TipoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoMembresiaRepository extends JpaRepository<TipoMembresia, Long> {

    List<TipoMembresia> findByActivoTrue();

    @Query("SELECT t FROM TipoMembresia t WHERE t.activo = true ORDER BY t.precio ASC")
    List<TipoMembresia> findActivosOrderByPrecio();

    boolean existsByNombre(String nombre);
}
