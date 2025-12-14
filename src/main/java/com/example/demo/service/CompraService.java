package com.example.demo.service;

import com.example.demo.domain.Cliente;
import com.example.demo.domain.Detalle;
import com.example.demo.domain.Encabezado;
import com.example.demo.domain.Producto;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetalleRepository;
import com.example.demo.repository.EncabezadoRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompraService {

    private final EncabezadoRepository encabezadoRepository;
    private final DetalleRepository detalleRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final EtlService etlService;

    public CompraService(EncabezadoRepository encabezadoRepository,
                         DetalleRepository detalleRepository,
                         ClienteRepository clienteRepository,
                         ProductoRepository productoRepository,
                         EtlService etlService) {
        this.encabezadoRepository = encabezadoRepository;
        this.detalleRepository = detalleRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.etlService = etlService;
    }

    @Transactional
    public Encabezado registrarCompra(Long clienteId, List<Detalle> detalles) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Encabezado encabezado = new Encabezado();
        encabezado.setCliente(cliente);

        double totalSinDescuento = 0.0;
        double descuentoTotal = 0.0;

        for (Detalle detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar stock
            if (producto.getCantidad() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            detalle.setEncabezado(encabezado);
            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(detalle.getCantidad() * producto.getPrecio());

            // Acumular total
            totalSinDescuento += detalle.getSubtotal();

            // Reducir stock del producto
            producto.setCantidad(producto.getCantidad() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        // 🚨 Aquí puedes meter la lógica de descuentos
        descuentoTotal = totalSinDescuento * 0.1; // 10% de descuento ejemplo
        double totalConDescuento = totalSinDescuento - descuentoTotal;

        encabezado.setTotalSinDescuento(totalSinDescuento);
        encabezado.setDescuentoTotal(descuentoTotal);
        encabezado.setTotalConDescuento(totalConDescuento);

        // Guardar encabezado y detalles
        Encabezado encabezadoGuardado = encabezadoRepository.save(encabezado);
        detalleRepository.saveAll(detalles);
            // Ejecutar ETL después de la compra
            etlService.runAllEtls();

        return encabezadoGuardado;
    }
}

