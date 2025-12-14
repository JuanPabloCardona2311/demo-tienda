// package com.example.demo.web;

// import com.example.demo.domain.Detalle;
// import com.example.demo.domain.Encabezado;
// import com.example.demo.service.CompraService;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.util.Arrays;
// import java.util.List;

// @Controller
// @RequestMapping("/compras")
// public class CompraController {

//     private final CompraService compraService;

//     public CompraController(CompraService compraService) {
//         this.compraService = compraService;
//     }

//     // Mostrar formulario para registrar compra
//     @GetMapping("/nueva")
//     public String mostrarFormulario() {
//         return "nueva_compra"; // deberías crear nueva_compra.html
//     }

//     // Procesar registro de compra
//     @PostMapping("/registrar")
//     public String registrarCompra(@RequestParam Long clienteId,
//                                   @RequestParam Long productoId,
//                                   @RequestParam Integer cantidad,
//                                   Model model) {
//         Detalle detalle = new Detalle();
//         detalle.setCantidad(cantidad);
//         detalle.setProducto(new com.example.demo.domain.Producto());
//         detalle.getProducto().setId(productoId);

//         List<Detalle> detalles = Arrays.asList(detalle);

//         Encabezado compra = compraService.registrarCompra(clienteId, detalles);

//         model.addAttribute("compra", compra);
//         return "compra_exitosa"; // deberías crear compra_exitosa.html
//     }
// }

package com.example.demo.web;

import com.example.demo.domain.Detalle;
import com.example.demo.domain.Encabezado;
import com.example.demo.domain.Producto;
import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.CompraService;
import com.example.demo.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/compras")
public class CompraController {

    private final CompraService compraService;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;

    public CompraController(CompraService compraService, UsuarioRepository usuarioRepository, ProductoService productoService) {
        this.compraService = compraService;
        this.usuarioRepository = usuarioRepository;
        this.productoService = productoService;
    }

    // Mostrar formulario de compra: ahora añade la lista de productos disponibles al modelo
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model, java.security.Principal principal) {
        model.addAttribute("encabezado", new Encabezado());

        // Productos disponibles para mostrar en la vista
        model.addAttribute("productos", productoService.listarProductos());

        // Si hay usuario autenticado, también pasar info del cliente asociado (si existe)
        if (principal != null) {
            usuarioRepository.findByUsername(principal.getName()).ifPresent(u -> model.addAttribute("cliente", u.getCliente()));
        }

        return "nueva_compra";
    }

    // Procesar compra (cliente se obtiene del usuario autenticado)
    @PostMapping("/registrar")
    public String registrarCompra(@RequestParam Long productoId,
                                  @RequestParam Integer cantidad,
                                  Principal principal,
                                  Model model) {

        // ✅ Obtener cliente autenticado de forma segura con Optional
        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + principal.getName()));

        Long clienteId = usuario.getCliente().getId();

        // Crear detalle
        Detalle detalle = new Detalle();
        detalle.setCantidad(cantidad);
        Producto producto = new Producto();
        producto.setId(productoId);
        detalle.setProducto(producto);

        List<Detalle> detalles = Arrays.asList(detalle);

        // Guardar compra
        Encabezado compra = compraService.registrarCompra(clienteId, detalles);

        // Enviar compra a la vista
        model.addAttribute("compra", compra);
        return "compra_exitosa";
    }
}


