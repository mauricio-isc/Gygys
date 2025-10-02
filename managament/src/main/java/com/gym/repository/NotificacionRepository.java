package com.gym.repository;

import com.gym.entity.Miembro;
import com.gym.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByMiembro(Miembro miembro);

    List<Notificacion> findByMiembroOrderByFechaEnvioDesc(Miembro miembro);

    List <Notificacion> findByLeida(Boolean leida);

    List <Notificacion> findByEnviada(Boolean enviada);

    List<Notificacion> findByTipoNotificacion(Notificacion.TipoNotificacion tipoNotificacion);

    @Query("SELECT n FROM Notificacion n WHERE n.leida = false AND n.fechaProgramada <= :fechaActual")
    List<Notificacion> findPendientesPorEnviar(@Param("fechaActual")LocalDateTime fechaActual);

    @Query("SELECT n FROM Notificacion n" +
        "JOIN FETCH n.miembro m"+
        "WHERE n.leida = false"+
        "ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findNoLeidasWithMiembro();

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.leida = false")
    long countNoLeidas();

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.enviada = false")
    long countNoEnviadas();

    @Query("SELECT n FROM Notificacion n"+
        "JOIN FETCH n.miembro m"+
        "ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findAllWithMiembroOrderByFechaEnvioDesc();

    @Query("SELECT n FROM Notificacion n"+
    "JOIN FETCH n.miembro m"+
    "WHERE n.fechaEnvio >= :fechaInicio"+
    "ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findByFechaEnvioAfterWithMiembro(
            @Param("fechaInicio") LocalDateTime fechaInicio
    );

}
