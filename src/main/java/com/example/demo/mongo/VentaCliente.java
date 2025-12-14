package com.example.demo.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ventas_por_cliente")
public class VentaCliente {

    @Id
    private String id;

    private Long clienteId;
    private String nombre;
    private String apellido;
    private Double totalGastado;
    private Long compras;
    private LocalDateTime ultimaCompra;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Double getTotalGastado() { return totalGastado; }
    public void setTotalGastado(Double totalGastado) { this.totalGastado = totalGastado; }

    public Long getCompras() { return compras; }
    public void setCompras(Long compras) { this.compras = compras; }

    public LocalDateTime getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(LocalDateTime ultimaCompra) { this.ultimaCompra = ultimaCompra; }
}
