package com.example.demo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VentaClienteRepository extends MongoRepository<VentaCliente, String> {
    Optional<VentaCliente> findByClienteId(Long clienteId);
}
