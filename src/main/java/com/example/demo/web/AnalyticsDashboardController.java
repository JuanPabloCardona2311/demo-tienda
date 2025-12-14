
package com.example.demo.web;

import com.example.demo.service.AnalyticsService;
import com.example.demo.service.AnalyticsMongoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsDashboardController {

    private final AnalyticsService analyticsService;
    private final AnalyticsMongoService analyticsMongoService;

    public AnalyticsDashboardController(AnalyticsService analyticsService, AnalyticsMongoService analyticsMongoService) {
        this.analyticsService = analyticsService;
        this.analyticsMongoService = analyticsMongoService;
    }
    @GetMapping("/top-productos")
    public ResponseEntity<List<Map<String, Object>>> topProductos() {
        List<Map<String, Object>> res = analyticsMongoService.topProductos().stream().map(p -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("nombre", p.getNombre());
            map.put("totalVendido", p.getTotalVendido());
            map.put("cantidadVendida", p.getCantidadVendida());
            map.put("ultimaVenta", p.getUltimaVenta() == null ? null : p.getUltimaVenta().toString());
            return map;
        }).toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<List<Map<String, Object>>> topClientes() {
        List<Map<String, Object>> res = analyticsMongoService.topClientes().stream().map(c -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("nombre", c.getNombre());
            map.put("apellido", c.getApellido());
            map.put("totalGastado", c.getTotalGastado());
            map.put("compras", c.getCompras());
            map.put("ultimaCompra", c.getUltimaCompra() == null ? null : c.getUltimaCompra().toString());
            return map;
        }).toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/stock/low")
    public ResponseEntity<List<Map<String, ?>>> lowStock(@RequestParam(defaultValue = "10") int threshold) {
        List<Map<String, ?>> res = analyticsService.lowStock(threshold).stream().map(p -> Map.of(
                "id", p.getId(),
                "nombre", p.getNombre(),
                "cantidad", p.getCantidad(),
                "precio", p.getPrecio(),
                "valorInventario", p.getCantidad() * p.getPrecio()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/productos/nuevos")
    public ResponseEntity<List<Map<String, ?>>> recentProducts(@RequestParam(defaultValue = "7") int days) {
        List<Map<String, ?>> res = analyticsService.recentProducts(days).stream().map(p -> Map.of(
                "id", p.getId(),
                "nombre", p.getNombre(),
                "cantidad", p.getCantidad(),
                "precio", p.getPrecio(),
                "createdAt", p.getCreatedAt() == null ? null : p.getCreatedAt().toString()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/productos/top-por-precio")
    public ResponseEntity<List<Map<String, ?>>> topByPrice(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, ?>> res = analyticsService.topProductsByPrice(limit).stream().map(p -> Map.of(
                "id", p.getId(),
                "nombre", p.getNombre(),
                "precio", p.getPrecio(),
                "cantidad", p.getCantidad()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/clientes/activos")
    public ResponseEntity<List<Map<String, ?>>> activeClients(@RequestParam(defaultValue = "30") int days) {
        List<Map<String, ?>> res = analyticsService.activeClients(days).stream().map(c -> Map.of(
                "id", c.getId(),
                "nombre", c.getNombre(),
                "apellido", c.getApellido(),
                "telefono", c.getTelefono(),
                "createdAt", c.getCreatedAt() == null ? null : c.getCreatedAt().toString()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

}
