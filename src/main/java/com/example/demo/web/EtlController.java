package com.example.demo.web;

import com.example.demo.service.EtlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/etl")
public class EtlController {

    private final EtlService etlService;

    public EtlController(EtlService etlService) {
        this.etlService = etlService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runEtl() {
        etlService.etlVentasPorDia();
        etlService.etlVentasPorProducto();
        etlService.etlVentasPorCliente();
        return ResponseEntity.ok("ETL ejecutado");
    }
}
