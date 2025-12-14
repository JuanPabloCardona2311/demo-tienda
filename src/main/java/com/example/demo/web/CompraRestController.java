package com.example.demo.web;

import com.example.demo.domain.Detalle;
import com.example.demo.domain.Encabezado;
import com.example.demo.domain.Producto;
import com.example.demo.domain.Usuario;
import com.example.demo.service.CompraService;
import com.example.demo.service.ProductoService;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.domain.Cliente;
import com.example.demo.repository.ClienteRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras")
public class CompraRestController {

    private final CompraService compraService;
    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public CompraRestController(CompraService compraService, ProductoService productoService, UsuarioRepository usuarioRepository, ClienteRepository clienteRepository) {
        this.compraService = compraService;
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    // Listar productos simples para mostrar en UI/cliente
    @GetMapping("/productos")
    public ResponseEntity<?> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    // Crear compra sencilla usando usuario autenticado y un producto + cantidad
    @PostMapping
    public ResponseEntity<?> crearCompra(@RequestBody PurchaseRequest req, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + principal.getName()));

        if (usuario.getCliente() == null) {
            // Crear un cliente mínimo automáticamente para que la compra pueda registrarse
            Cliente nuevo = new Cliente();
            nuevo.setNombre(usuario.getUsername());
            nuevo.setApellido("");
            // Generar un teléfono único temporal para evitar violación de unique
            String telefonoAuto = "auto-" + (usuario.getId() != null ? usuario.getId() : "u") + "-" + System.currentTimeMillis();
            nuevo.setTelefono(telefonoAuto);
            nuevo.setDireccion("Sin dirección");
            nuevo.setUsuario(usuario);
            clienteRepository.save(nuevo);
            // asociar en memoria para uso inmediato
            usuario.setCliente(nuevo);
        }

        // Construir detalle mínimo: sólo indica el producto.id y la cantidad. CompraService se encargará de completar precio y subtotal
        Detalle detalle = new Detalle();
        Producto p = new Producto();
        p.setId(req.getProductoId());
        detalle.setProducto(p);
        detalle.setCantidad(req.getCantidad());

    Encabezado encabezado = compraService.registrarCompra(usuario.getCliente().getId(), Arrays.asList(detalle));

    // Devolver sólo un JSON mínimo para evitar problemas de serialización por referencias cíclicas
    return ResponseEntity.created(URI.create("/api/encabezados/" + encabezado.getId()))
        .body(Map.of("id", encabezado.getId()));
    }

    public static class PurchaseRequest {
        @NotNull
        private Long productoId;

        @NotNull
        @Min(1)
        private Integer cantidad;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }

    // Checkout: recibir una lista de items y crear una compra con varios detalles
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody List<PurchaseRequest> items, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body("Lista de productos vacía");
        }

        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + principal.getName()));

        if (usuario.getCliente() == null) {
            // Crear cliente mínimo automáticamente (igual que en crearCompra)
            Cliente nuevo = new Cliente();
            nuevo.setNombre(usuario.getUsername());
            nuevo.setApellido("");
            String telefonoAuto = "auto-" + (usuario.getId() != null ? usuario.getId() : "u") + "-" + System.currentTimeMillis();
            nuevo.setTelefono(telefonoAuto);
            nuevo.setDireccion("Sin dirección");
            nuevo.setUsuario(usuario);
            clienteRepository.save(nuevo);
            usuario.setCliente(nuevo);
        }

        List<Detalle> detalles = new ArrayList<>();
        for (PurchaseRequest pr : items) {
            Detalle d = new Detalle();
            com.example.demo.domain.Producto p = new com.example.demo.domain.Producto();
            p.setId(pr.getProductoId());
            d.setProducto(p);
            d.setCantidad(pr.getCantidad());
            detalles.add(d);
        }

    Encabezado encabezado = compraService.registrarCompra(usuario.getCliente().getId(), detalles);

    // Devolver sólo el id para evitar serializar entidades con relaciones cíclicas
    return ResponseEntity.created(URI.create("/api/encabezados/" + encabezado.getId()))
        .body(Map.of("id", encabezado.getId()));
    }
}
