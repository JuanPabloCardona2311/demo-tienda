package com.example.demo.repository;

import com.example.demo.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscar usuario por nombre de usuario
    Optional<Usuario> findByUsername(String username);

    // Verificar si un email ya existe
    boolean existsByEmail(String email);
}

