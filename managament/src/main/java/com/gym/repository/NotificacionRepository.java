package com.gym.repository;

import com.gym.entity.Miembro;
import com.gym.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByMiembro(Miembro miembro);

    List<Notificacion> findByMiembroOrderByFechaEnvioDesc(Miembro miembro);

    List <Notificacion> findByLeida(Boolean leida);

    List <Notificacion> findByEnviada(Boolean enviada);

    List <Notificacion> findByTipoNotificacion(Notificacion, Notificacion.TipoNotificacion tipoNotificacion);
    
}
