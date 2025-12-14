package com.example.demo.service;

import com.example.demo.mongo.VentaDia;
import com.example.demo.mongo.VentaDiaRepository;
import com.example.demo.mongo.VentaProductoRepository;
import com.example.demo.mongo.VentaProducto;
import com.example.demo.mongo.VentaClienteRepository;
import com.example.demo.mongo.VentaCliente;
import com.example.demo.repository.EncabezadoRepository;
import com.example.demo.repository.DetalleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class EtlService {
    /**
     * Ejecuta todos los ETLs: por día, por producto y por cliente.
     */
    public void runAllEtls() {
        etlVentasPorDia();
        etlVentasPorProducto();
        etlVentasPorCliente();
    }

    private final EncabezadoRepository encabezadoRepo;
    private final DetalleRepository detalleRepo;
    private final VentaDiaRepository ventaDiaRepo;
    private final VentaProductoRepository ventaProductoRepo;
    private final VentaClienteRepository ventaClienteRepo;

    public EtlService(EncabezadoRepository encabezadoRepo,
                      DetalleRepository detalleRepo,
                      VentaDiaRepository ventaDiaRepo,
                      VentaProductoRepository ventaProductoRepo,
                      VentaClienteRepository ventaClienteRepo) {
        this.encabezadoRepo = encabezadoRepo;
        this.detalleRepo = detalleRepo;
        this.ventaDiaRepo = ventaDiaRepo;
        this.ventaProductoRepo = ventaProductoRepo;
        this.ventaClienteRepo = ventaClienteRepo;
    }

    /**
     * ETL para generar ventas agregadas por día.
     * Programado para ejecutarse cada hora. Ajusta el cron según necesidad.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void etlVentasPorDia() {
        // Rango: último dia
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = hasta.minusDays(1);

        List<Object[]> agg = encabezadoRepo.sumTotalGroupedByDate(desde, hasta);
        for (Object[] row : agg) {
            if (row == null || row.length < 3) continue;

            // row[0] puede ser java.sql.Date, java.sql.Timestamp o java.lang.String dependiendo del dialecto
            Object oFecha = row[0];
            final LocalDate fecha = toLocalDate(oFecha);
            if (fecha == null) continue;

            final Double total = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
            final Integer count = row[2] == null ? 0 : ((Number) row[2]).intValue();

            VentaDia vd = ventaDiaRepo.findFirstByFecha(fecha).orElseGet(() -> {
                VentaDia v = new VentaDia();
                v.setFecha(fecha);
                return v;
            });
            vd.setTotal(total);
            vd.setCantidadVentas(count);
            ventaDiaRepo.save(vd);
        }
    }

    @Scheduled(cron = "0 10 * * * *")
    @Transactional(readOnly = true)
    public void etlVentasPorProducto() {
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = hasta.minusDays(1); 

        List<Object[]> agg = detalleRepo.sumByProductBetween(desde, hasta);
        for (Object[] row : agg) {
            if (row == null || row.length < 5) continue;
            final Long productoId = row[0] == null ? null : ((Number) row[0]).longValue();
            final String nombre = row[1] == null ? null : row[1].toString();
            final Double total = row[2] == null ? 0.0 : ((Number) row[2]).doubleValue();
            final Long qty = row[3] == null ? 0L : ((Number) row[3]).longValue();
            final Object oDate = row[4];
            final LocalDateTime ultima = toLocalDateTime(oDate);

            if (productoId == null) continue;
            VentaProducto vp = ventaProductoRepo.findByProductoId(productoId).orElseGet(() -> {
                VentaProducto v = new VentaProducto();
                v.setProductoId(productoId);
                v.setNombre(nombre);
                return v;
            });
            vp.setTotalVendido(total);
            vp.setCantidadVendida(qty);
            vp.setUltimaVenta(ultima);
            ventaProductoRepo.save(vp);
        }
    }

    @Scheduled(cron = "0 20 * * * *")
    @Transactional(readOnly = true)
    public void etlVentasPorCliente() {
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = hasta.minusDays(1);

        List<Object[]> agg = encabezadoRepo.sumByClientBetween(desde, hasta);
        for (Object[] row : agg) {
            if (row == null || row.length < 6) continue;
            final Long clienteId = row[0] == null ? null : ((Number) row[0]).longValue();
            final String nombre = row[1] == null ? null : row[1].toString();
            final String apellido = row[2] == null ? null : row[2].toString();
            final Double total = row[3] == null ? 0.0 : ((Number) row[3]).doubleValue();
            final Long cnt = row[4] == null ? 0L : ((Number) row[4]).longValue();
            final Object oDate = row[5];
            final LocalDateTime ultima = toLocalDateTime(oDate);

            if (clienteId == null) continue;
            VentaCliente vc = ventaClienteRepo.findByClienteId(clienteId).orElseGet(() -> {
                VentaCliente v = new VentaCliente();
                v.setClienteId(clienteId);
                v.setNombre(nombre);
                v.setApellido(apellido);
                return v;
            });
            vc.setTotalGastado(total);
            vc.setCompras(cnt);
            vc.setUltimaCompra(ultima);
            ventaClienteRepo.save(vc);
        }
    }

    private LocalDateTime toLocalDateTime(Object o) {
        if (o == null) return null;
        if (o instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (o instanceof java.sql.Date) {
            return ((java.sql.Date) o).toLocalDate().atStartOfDay();
        } else if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        } else if (o instanceof LocalDate) {
            return ((LocalDate) o).atStartOfDay();
        } else if (o instanceof String) {
            try {
                return LocalDateTime.parse((String) o);
            } catch (Exception ex) {
                try {
                    LocalDate d = LocalDate.parse((String) o);
                    return d.atStartOfDay();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    private LocalDate toLocalDate(Object oFecha) {
        if (oFecha == null) return null;
        if (oFecha instanceof Date) {
            return ((Date) oFecha).toLocalDate();
        } else if (oFecha instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) oFecha).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (oFecha instanceof LocalDate) {
            return (LocalDate) oFecha;
        } else if (oFecha instanceof String) {
            try {
                return LocalDate.parse((String) oFecha);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }
}
