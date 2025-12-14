package com.example.demo.web;

import com.example.demo.mongo.VentaDia;
import com.example.demo.mongo.VentaDiaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final VentaDiaRepository ventaDiaRepository;

    public AnalyticsController(VentaDiaRepository ventaDiaRepository) {
        this.ventaDiaRepository = ventaDiaRepository;
    }

    // Devuelve todas las ventas agregadas por día
    @GetMapping("/ventas/dia")
    public ResponseEntity<List<VentaDia>> ventasPorDia() {
        List<VentaDia> all = ventaDiaRepository.findAll();
        return ResponseEntity.ok(all);
    }

    // Devuelve ventas por fecha (formato ISO: yyyy-MM-dd)
    @GetMapping("/ventas/dia/{fecha}")
    public ResponseEntity<?> ventasPorFecha(@PathVariable String fecha) {
        LocalDate d;
        try {
            d = LocalDate.parse(fecha);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use yyyy-MM-dd");
        }

        return ventaDiaRepository.findFirstByFecha(d)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
