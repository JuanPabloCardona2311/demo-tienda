package com.example.demo.web;

import com.example.demo.domain.Cliente;
import com.example.demo.service.ClienteService;
import com.example.demo.repository.UsuarioRepository;
import java.security.Principal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // GET: Listar todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    // POST: Crear cliente
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente creado = clienteService.crearCliente(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + creado.getId())).body(creado);
    }

    // POST: Crear cliente para el usuario autenticado (útil desde la UI)
    @PostMapping("/me")
    public ResponseEntity<?> crearClienteParaUsuario(@Valid @RequestBody Cliente cliente, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        // Buscar usuario autenticado
    com.example.demo.domain.Usuario usuario = usuarioRepository.findByUsername(principal.getName())
        .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        if (usuario.getCliente() != null) {
            return ResponseEntity.badRequest().body("El usuario ya tiene un cliente asociado");
        }

        // Asociar y crear cliente
        cliente.setUsuario(usuario);
        Cliente creado = clienteService.crearCliente(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + creado.getId())).body(creado);
    }

    // GET: Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable Long id) {
        return clienteService.obtenerCliente(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente clienteDetalles) {
        return clienteService.obtenerCliente(id)
                .map(cliente -> {
                    cliente.setNombre(clienteDetalles.getNombre());
                    cliente.setApellido(clienteDetalles.getApellido());
                    cliente.setTelefono(clienteDetalles.getTelefono());
                    cliente.setDireccion(clienteDetalles.getDireccion());
                    // ⚠️ Nota: No se actualizan createdAt ni usuario
                    Cliente actualizado = clienteService.crearCliente(cliente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (clienteService.obtenerCliente(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}

