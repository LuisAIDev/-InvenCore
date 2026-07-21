package com.invencore.app.controller;

import com.invencore.app.model.dto.OfertaDTO;
import com.invencore.app.service.OfertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OfertaController {

    private final OfertaService ofertaService;

    @GetMapping
    public ResponseEntity<List<OfertaDTO>> listar() {
        return ResponseEntity.ok(ofertaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfertaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ofertaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OfertaDTO> crear(@Valid @RequestBody OfertaDTO dto) {
        return ResponseEntity.ok(ofertaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfertaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody OfertaDTO dto) {
        return ResponseEntity.ok(ofertaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ofertaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
