package com.gym.repository;

import com.gym.entity.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Long> {

    Optional<ConfiguracionSistema> findByClave(String clave);

    boolean existsByClave(String clave);
}
