package com.gym.repository;

import com.gym.entity.Membresia;
import com.gym.entity.Pago;
import com.gym.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByMembresia(Membresia membresia);

    List<Pago> findByRegistradoPor(Usuario registradoPor);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.fechaPago >= :fechaInicio")
    BigDecimal sumMontoByFechaPagoAfter(@Param("fechaInicio")LocalDateTime fechaInicio);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE YEAR(p.fechaPago) = :year")
    BigDecimal sumMontoByYear(@Param("year") int year);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE MONTH(p.fechaPago) = :month AND YEAR(p.fechaPago) = :year")
    BigDecimal sumMontoByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT p FROM Pago p "+
        " JOIN FETCH p.membresia m "+
        " JOIN FETCH m.miembro mi "+
        " JOIN FETCH p.registradoPor u "+
        " ORDER BY p.fechaPago DESC")
    List<Pago> findAllWithDetailsOrderByFechaPagoDesc();

    @Query("SELECT p FROM Pago p "+
    " JOIN FETCH p.membresia m "+
    " JOIN FETCH m.miembro mi "+
    " JOIN FETCH p.registradoPor u "+
    " WHERE p.fechaPago >= :fechaInicio "+
    " ORDER BY p.fechaPago DESC")
    List<Pago> findByFechaPagoAfterWithDetails(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.fechaPago >= :fechaInicio")
    long countByFechaPagoAfter(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT p.metodoPago, COUNT(p), SUM(p.monto)"+
    " FROM Pago p "+
    " WHERE p.fechaPago >= :fechaInicio "+
    " GROUP BY p.metodoPago")
    List<Object[]> countAndSumByMetodoPagoAndFechaPagoAfter(
            @Param("fechaInicio") LocalDateTime fechaInicio
    );

}
