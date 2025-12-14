package com.example.demo.service;

import com.example.demo.mongo.VentaProducto;
import com.example.demo.mongo.VentaCliente;
import com.example.demo.mongo.VentaProductoRepository;
import com.example.demo.mongo.VentaClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsMongoService {
    private final VentaProductoRepository ventaProductoRepository;
    private final VentaClienteRepository ventaClienteRepository;

    public AnalyticsMongoService(VentaProductoRepository ventaProductoRepository,
                                VentaClienteRepository ventaClienteRepository) {
        this.ventaProductoRepository = ventaProductoRepository;
        this.ventaClienteRepository = ventaClienteRepository;
    }

    public List<VentaProducto> topProductos() {
        return ventaProductoRepository.findAll();
    }

    public List<VentaCliente> topClientes() {
        return ventaClienteRepository.findAll();
    }
}
