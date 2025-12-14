package com.example.demo.repository;

import com.example.demo.domain.VentaCliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface VentaClienteRepository extends MongoRepository<VentaCliente, String> {
}
