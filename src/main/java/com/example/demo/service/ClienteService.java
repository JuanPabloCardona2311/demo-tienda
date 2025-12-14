package com.example.demo.service;

import com.example.demo.domain.Cliente;
import com.example.demo.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Listar clientes
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Obtener cliente por id
    public Optional<Cliente> obtenerCliente(Long id) {
        return clienteRepository.findById(id);
    }

    // Crear cliente con validación
    public Cliente crearCliente(Cliente cliente) {
        if (clienteRepository.existsByTelefono(cliente.getTelefono())) {
            throw new RuntimeException("El teléfono ya está registrado");
        }
        return clienteRepository.save(cliente);
    }

    // Eliminar cliente
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}

