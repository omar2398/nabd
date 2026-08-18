package com.nabd.ingestion_service.controller;

import com.nabd.ingestion_service.dto.EnergyUsageDTO;
import com.nabd.ingestion_service.service.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/ingestion")
@RestController
public class IngestionController {
    private final IngestionService service;

    public IngestionController(IngestionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void ingestData(@RequestBody EnergyUsageDTO request){
        service.ingestEnergyUsage(request);
    }
}
