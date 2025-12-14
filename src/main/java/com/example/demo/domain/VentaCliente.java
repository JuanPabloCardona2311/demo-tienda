package com.example.demo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ventas_por_cliente")
public class VentaCliente {
    @Id
    private String id;
    private String nombre;
    private String apellido;
    private double totalGastado;
    private int compras;
    private LocalDateTime ultimaCompra;

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public double getTotalGastado() { return totalGastado; }
    public void setTotalGastado(double totalGastado) { this.totalGastado = totalGastado; }
    public int getCompras() { return compras; }
    public void setCompras(int compras) { this.compras = compras; }
    public LocalDateTime getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(LocalDateTime ultimaCompra) { this.ultimaCompra = ultimaCompra; }
}
