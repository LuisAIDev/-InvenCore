package com.invencore.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check del sistema")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica que el servicio esté operativo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servicio operativo")
    })
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "app", "InvenCore",
            "version", "1.0.0"
        ));
    }
}
