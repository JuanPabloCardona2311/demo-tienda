package com.example.demo.redis;

import org.springframework.data.repository.CrudRepository;

public interface UsuarioRedisRepository extends CrudRepository<UsuarioRedis, Long> {
    UsuarioRedis findByUsername(String username);
}
