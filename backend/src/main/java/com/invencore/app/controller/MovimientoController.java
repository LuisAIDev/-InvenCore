package com.invencore.app.controller;

import com.invencore.app.model.dto.MovimientoDTO;
import com.invencore.app.model.entity.TipoMovimiento;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<Page<MovimientoDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarTodos(pageable));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Page<MovimientoDTO>> listarPorProducto(
            @PathVariable Long productoId,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(productoId, pageable));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<MovimientoDTO>> listarPorTipo(
            @PathVariable TipoMovimiento tipo,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarPorTipo(tipo, pageable));
    }

    @GetMapping("/fechas")
    public ResponseEntity<Page<MovimientoDTO>> listarPorFechas(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarPorFechas(inicio, fin, pageable));
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrar(
            @Valid @RequestBody MovimientoDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrar(dto, usuario.getId()));
    }
}
