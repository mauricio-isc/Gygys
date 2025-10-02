package com.gym.repository;

import com.gym.entity.ConfiguracionSistema;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionSistemaRepository {

    Optional<ConfiguracionSistema> findByClave(String clave);

    boolean existsByClave(String clave);
}
