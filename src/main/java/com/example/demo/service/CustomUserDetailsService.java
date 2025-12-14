package com.example.demo.service;

import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.redis.UsuarioRedis;
import com.example.demo.redis.UsuarioRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioRedisRepository usuarioRedisRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 1. Buscar en Redis con manejo de fallback
    try {
        UsuarioRedis usuarioRedis = usuarioRedisRepository.findByUsername(username);
        if (usuarioRedis != null) {
            String roleName = (usuarioRedis.getRol() != null) ? usuarioRedis.getRol().name() : "USER";
            return User.builder()
                    .username(usuarioRedis.getUsername())
                    .password(usuarioRedis.getPassword())
                    .roles(roleName)
                    .build();
        }
    } catch (Exception e) {
        System.out.println("[WARN] Redis no disponible al cargar usuario: " + e.getMessage());
    }

    // 2. Buscar en MySQL
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    // Guardar en Redis para futuras consultas rápidas (si está disponible)
    try {
        UsuarioRedis ur = new UsuarioRedis();
        ur.setId(usuario.getId());
        ur.setUsername(usuario.getUsername());
        ur.setPassword(usuario.getPassword());
        ur.setRol(usuario.getRol());
        usuarioRedisRepository.save(ur);
    } catch (Exception e) {
        System.out.println("[WARN] No se pudo guardar usuario en Redis: " + e.getMessage());
    }

    return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(usuario.getRol() != null ? usuario.getRol().name() : "USER")
            .build();
    }
}
