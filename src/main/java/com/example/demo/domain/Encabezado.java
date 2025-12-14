package com.example.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "encabezados")
public class Encabezado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDateTime fecha;

    private Double totalSinDescuento;
    private Double descuentoTotal;
    private Double totalConDescuento;

    @OneToMany(mappedBy = "encabezado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Detalle> detalles;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    // ---------- GETTERS & SETTERS ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getTotalSinDescuento() {
        return totalSinDescuento;
    }

    public void setTotalSinDescuento(Double totalSinDescuento) {
        this.totalSinDescuento = totalSinDescuento;
    }

    public Double getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(Double descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public Double getTotalConDescuento() {
        return totalConDescuento;
    }

    public void setTotalConDescuento(Double totalConDescuento) {
        this.totalConDescuento = totalConDescuento;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }
}

