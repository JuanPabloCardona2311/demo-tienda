package com.example.demo.redis;

import com.example.demo.domain.Rol;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;

@RedisHash("Usuario")
public class UsuarioRedis implements Serializable {
    @Id
    private Long id;
    private String username;
    private String password;
    private Rol rol; // guardamos el rol para mantener permisos al cachear

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
