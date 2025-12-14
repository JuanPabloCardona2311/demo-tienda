package com.example.demo.repository;

import com.example.demo.domain.VentaProducto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface VentaProductoRepository extends MongoRepository<VentaProducto, String> {
}
