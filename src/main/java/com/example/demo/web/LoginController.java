package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.demo.service.UsuarioService;
import com.example.demo.domain.Usuario;
import com.example.demo.domain.Cliente;
import com.example.demo.domain.Rol;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            RedirectAttributes redirectAttributes) {
        
        // Validaciones previas
        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario es requerido");
            return "redirect:/registro";
        }
        
        if (password == null || password.trim().isEmpty() || password.length() < 4) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 4 caracteres");
            return "redirect:/registro";
        }
        
        if (usuarioRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe en la base de datos");
            return "redirect:/registro";
        }
        
        if (usuarioRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya existe en la base de datos");
            return "redirect:/registro";
        }
        
        try {
            // Crear el usuario
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(password);
            usuario.setEmail(email);
            usuario.setRol(Rol.CLIENTE);
            usuario.setEnabled(true);
            
            usuario = usuarioService.crearUsuario(usuario);
            
            // Crear automáticamente el cliente asociado
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setTelefono(telefono != null && !telefono.isEmpty() ? telefono : "N/A");
            cliente.setDireccion(direccion != null && !direccion.isEmpty() ? direccion : "N/A");
            cliente.setUsuario(usuario);
            clienteRepository.save(cliente);
            
            redirectAttributes.addFlashAttribute("mensaje", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            String errorMsg = "Error al registrar: " + e.getMessage();
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/registro";
        }
    }
}


