package com.example.demo.repository;

import com.example.demo.domain.Encabezado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EncabezadoRepository extends JpaRepository<Encabezado, Long> {

	/**
	 * Agrupa totales por fecha (solo fecha, sin hora) entre dos instantes.
	 * Devuelve lista de objetos: [java.time.LocalDate fecha, Double total, Long count]
	 */
	@Query("SELECT FUNCTION('DATE', e.fecha) as fecha, SUM(e.totalConDescuento) as total, COUNT(e) as cnt " +
		   "FROM Encabezado e " +
		   "WHERE e.fecha BETWEEN :desde AND :hasta " +
		   "GROUP BY FUNCTION('DATE', e.fecha)")
	List<Object[]> sumTotalGroupedByDate(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

	@Query("SELECT DISTINCT e.cliente FROM Encabezado e WHERE e.fecha >= :since")
	List<com.example.demo.domain.Cliente> findDistinctClientesSince(@Param("since") LocalDateTime since);

	@Query("SELECT e.cliente.id as cid, e.cliente.nombre as nombre, e.cliente.apellido as apellido, SUM(e.totalConDescuento) as total, COUNT(e) as cnt, MAX(e.fecha) as lastDate " +
           "FROM Encabezado e WHERE e.fecha BETWEEN :desde AND :hasta GROUP BY e.cliente.id, e.cliente.nombre, e.cliente.apellido")
    List<Object[]> sumByClientBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

}

