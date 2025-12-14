package com.example.demo.config;

import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Si no existe un usuario con rol ADMIN, creamos uno por defecto
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@correo.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // 🔑 contraseña encriptada
            admin.setRol(Rol.ADMIN);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: admin / admin123");
        }
    }
}
