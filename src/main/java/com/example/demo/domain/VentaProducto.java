package com.example.demo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ventas_por_producto")
public class VentaProducto {
    @Id
    private String id;
    private String nombre;
    private double totalVendido;
    private int cantidadVendida;
    private LocalDateTime ultimaVenta;

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getTotalVendido() { return totalVendido; }
    public void setTotalVendido(double totalVendido) { this.totalVendido = totalVendido; }
    public int getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(int cantidadVendida) { this.cantidadVendida = cantidadVendida; }
    public LocalDateTime getUltimaVenta() { return ultimaVenta; }
    public void setUltimaVenta(LocalDateTime ultimaVenta) { this.ultimaVenta = ultimaVenta; }
}
