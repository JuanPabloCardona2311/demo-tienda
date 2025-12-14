package com.example.demo.service;

import com.example.demo.domain.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Listar todos los productos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Buscar producto por id
    public Optional<Producto> obtenerProducto(Long id) {
        return productoRepository.findById(id);
    }

    // Crear producto (aquí podemos agregar lógica extra)
    public Producto crearProducto(Producto producto) {
        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }
        return productoRepository.save(producto);
    }

    // Eliminar producto
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}
