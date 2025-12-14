package com.example.demo.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ventas_por_producto")
public class VentaProducto {

    @Id
    private String id;

    private Long productoId;
    private String nombre;
    private Double totalVendido;
    private Long cantidadVendida;
    private LocalDateTime ultimaVenta;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getTotalVendido() { return totalVendido; }
    public void setTotalVendido(Double totalVendido) { this.totalVendido = totalVendido; }

    public Long getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(Long cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public LocalDateTime getUltimaVenta() { return ultimaVenta; }
    public void setUltimaVenta(LocalDateTime ultimaVenta) { this.ultimaVenta = ultimaVenta; }
}
