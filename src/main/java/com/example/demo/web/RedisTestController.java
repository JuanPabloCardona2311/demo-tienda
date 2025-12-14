package com.example.demo.web;

import com.example.demo.redis.UsuarioRedis;
import com.example.demo.redis.UsuarioRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    @Autowired
    private UsuarioRedisRepository usuarioRedisRepository;

    // Endpoint para guardar manualmente un usuario en Redis
    @GetMapping("/test/redis/save")
    public String saveUsuarioRedis(@RequestParam Long id, @RequestParam String username, @RequestParam String password) {
        UsuarioRedis ur = new UsuarioRedis();
        ur.setId(id);
        ur.setUsername(username);
        ur.setPassword(password);
        usuarioRedisRepository.save(ur);
        return "Usuario guardado en Redis: id=" + id + ", username=" + username;
    }

    // Endpoint para ver un usuario en Redis
    @GetMapping("/test/redis/get")
    public UsuarioRedis getUsuarioRedis(@RequestParam Long id) {
        return usuarioRedisRepository.findById(id).orElse(null);
    }
}
