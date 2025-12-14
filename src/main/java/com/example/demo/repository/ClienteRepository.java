package com.example.demo.repository;

import com.example.demo.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByTelefono(String telefono);
}

