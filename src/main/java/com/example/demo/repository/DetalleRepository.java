package com.example.demo.repository;

import com.example.demo.domain.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleRepository extends JpaRepository<Detalle, Long> {

	@Query("SELECT d.producto.id as pid, d.producto.nombre as nombre, SUM(d.subtotal) as total, SUM(d.cantidad) as qty, MAX(d.encabezado.fecha) as lastDate " +
		   "FROM Detalle d WHERE d.encabezado.fecha BETWEEN :desde AND :hasta " +
		   "GROUP BY d.producto.id, d.producto.nombre")
	List<Object[]> sumByProductBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}

