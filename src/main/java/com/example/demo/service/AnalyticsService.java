package com.example.demo.service;

import com.example.demo.domain.Cliente;
import com.example.demo.domain.Producto;
import com.example.demo.repository.EncabezadoRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ProductoRepository productoRepository;
    private final EncabezadoRepository encabezadoRepository;

    public AnalyticsService(ProductoRepository productoRepository, EncabezadoRepository encabezadoRepository) {
        this.productoRepository = productoRepository;
        this.encabezadoRepository = encabezadoRepository;
    }

    public List<Producto> lowStock(int threshold) {
        return productoRepository.findByCantidadLessThanEqual(threshold);
    }

    public List<Producto> recentProducts(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(Math.max(1, days));
        return productoRepository.findByCreatedAtAfter(since);
    }

    public List<Producto> topProductsByPrice(int limit) {
        List<Producto> allSorted = productoRepository.findAll(Sort.by(Sort.Direction.DESC, "precio"));
        return allSorted.stream().limit(Math.max(1, limit)).collect(Collectors.toList());
    }

    public List<Cliente> activeClients(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(Math.max(1, days));
        return encabezadoRepository.findDistinctClientesSince(since);
    }

}
