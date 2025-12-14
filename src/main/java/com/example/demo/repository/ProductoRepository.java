package com.example.demo.repository;

import com.example.demo.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Verificar si un producto existe por nombre (ignora mayúsculas/minúsculas)
    boolean existsByNombreIgnoreCase(String nombre);

    List<Producto> findByCantidadLessThanEqual(int cantidad);

    List<Producto> findByCreatedAtAfter(LocalDateTime since);

    // Note: use inherited findAll(Sort) from JpaRepository / PagingAndSortingRepository
}
