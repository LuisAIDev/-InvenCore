package com.invencore.app.controller;

import com.invencore.app.model.dto.MovimientoDTO;
import com.invencore.app.model.entity.TipoMovimiento;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoDTO>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(productoId));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientoDTO>> listarPorTipo(@PathVariable TipoMovimiento tipo) {
        return ResponseEntity.ok(movimientoService.listarPorTipo(tipo));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<MovimientoDTO>> listarPorFechas(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.listarPorFechas(inicio, fin));
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrar(
            @Valid @RequestBody MovimientoDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrar(dto, usuario.getId()));
    }
}
