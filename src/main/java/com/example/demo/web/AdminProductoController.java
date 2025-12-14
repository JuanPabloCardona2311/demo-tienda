package com.example.demo.web;

import com.example.demo.domain.Producto;
import com.example.demo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/productos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String mostrarProductos(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        return "admin_productos";
    }

    @PostMapping
    public String agregarProducto(
            @RequestParam String nombre,
            @RequestParam Double precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes redirectAttributes) {
        try {
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setCantidad(stock);
            producto.setCategoria(categoria);
            producto.setDescripcion(descripcion);
            
            productoService.crearProducto(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/{id}/delete")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }
}
