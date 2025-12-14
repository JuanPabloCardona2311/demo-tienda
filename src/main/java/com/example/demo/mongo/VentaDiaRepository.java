package com.example.demo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface VentaDiaRepository extends MongoRepository<VentaDia, String> {
    // Use findFirstByFecha to avoid IncorrectResultSize when duplicates exist
    Optional<VentaDia> findFirstByFecha(LocalDate fecha);
}
