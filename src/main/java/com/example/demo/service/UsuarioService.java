package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.redis.UsuarioRedis;
import com.example.demo.redis.UsuarioRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioRedisRepository usuarioRedisRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por id
    public Optional<Usuario> obtenerUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    // Buscar usuario por username para login (Redis primero, luego MySQL)
    public Usuario loginPorUsername(String username, String password) {
        // 1. Buscar en Redis (con fallback si Redis falla)
        try {
            UsuarioRedis usuarioRedis = usuarioRedisRepository.findByUsername(username);
            if (usuarioRedis != null) {
                // Validar password
                if (passwordEncoder.matches(password, usuarioRedis.getPassword())) {
                    // Usuario y password correctos en Redis
                    return convertirAUsuario(usuarioRedis);
                } else {
                    throw new RuntimeException("Contraseña incorrecta");
                }
            }
        } catch (Exception e) {
            // Si falla Redis, registramos el incidente y seguimos con MySQL
            System.out.println("[WARN] Redis no disponible al buscar usuario: " + e.getMessage());
        }

        // 2. Buscar en MySQL
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                // Guardar en Redis para futuras consultas rápidas (si Redis está disponible)
                try {
                    UsuarioRedis ur = convertirAUsuarioRedis(usuario);
                    usuarioRedisRepository.save(ur);
                    System.out.println("[REDIS] Usuario guardado en Redis: id=" + ur.getId() + ", username=" + ur.getUsername());
                } catch (Exception e) {
                    System.out.println("[WARN] No se pudo guardar en Redis: " + e.getMessage());
                }
                return usuario;
            } else {
                throw new RuntimeException("Contraseña incorrecta");
            }
        }
        // 3. No existe en ninguna BD
        throw new RuntimeException("Usuario no encontrado");
    }

    // Conversión de Usuario a UsuarioRedis
    private UsuarioRedis convertirAUsuarioRedis(Usuario usuario) {
        UsuarioRedis ur = new UsuarioRedis();
        ur.setId(usuario.getId());
        ur.setUsername(usuario.getUsername());
        ur.setPassword(usuario.getPassword());
        ur.setRol(usuario.getRol());
        // Agrega otros campos si es necesario
        return ur;
    }

    // Conversión de UsuarioRedis a Usuario (solo para autenticación básica)
    private Usuario convertirAUsuario(UsuarioRedis ur) {
        Usuario usuario = new Usuario();
        usuario.setId(ur.getId());
        usuario.setUsername(ur.getUsername());
        usuario.setPassword(ur.getPassword());
        usuario.setRol(ur.getRol());
        // Agrega otros campos si es necesario
        return usuario;
    }

    // Crear usuario con validación y contraseña encriptada
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetalles) {
        return usuarioRepository.findById(id).map(usuario -> {

            // Validar que el email no esté en uso por otro usuario
            if (!usuario.getEmail().equals(usuarioDetalles.getEmail())
                    && usuarioRepository.existsByEmail(usuarioDetalles.getEmail())) {
                throw new RuntimeException("El email ya está en uso por otro usuario");
            }

            usuario.setUsername(usuarioDetalles.getUsername());
            usuario.setEmail(usuarioDetalles.getEmail());
            usuario.setRol(usuarioDetalles.getRol());
            usuario.setEnabled(usuarioDetalles.isEnabled());

            // Si vino password nuevo, lo encriptamos
            if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
            }

            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    // Eliminar usuario
    public void eliminarUsuario(Long id) {
    usuarioRepository.deleteById(id);
    try {
        usuarioRedisRepository.deleteById(id);
    } catch (Exception e) {
        System.out.println("[WARN] No se pudo eliminar usuario de Redis: " + e.getMessage());
    }
    }
}

