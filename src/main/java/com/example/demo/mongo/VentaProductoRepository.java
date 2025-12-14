package com.example.demo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VentaProductoRepository extends MongoRepository<VentaProducto, String> {
    Optional<VentaProducto> findByProductoId(Long productoId);
}
