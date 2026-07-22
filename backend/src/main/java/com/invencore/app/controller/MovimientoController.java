package com.invencore.app.controller;

import com.invencore.app.model.dto.MovimientoDTO;
import com.invencore.app.model.entity.TipoMovimiento;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Inventory", description = "Movimientos de inventario (ENTRADA / SALIDA)")
@SecurityRequirement(name = "bearerAuth")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    @Operation(summary = "Listar movimientos", description = "Retorna todos los movimientos paginados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de movimientos paginada"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Page<MovimientoDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarTodos(pageable));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar movimientos por producto",
               description = "Retorna los movimientos de un producto específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de movimientos del producto paginada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Page<MovimientoDTO>> listarPorProducto(
            @Parameter(description = "ID del producto") @PathVariable Long productoId,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(productoId, pageable));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar movimientos por tipo",
               description = "Retorna movimientos filtrados por tipo (ENTRADA / SALIDA)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de movimientos por tipo paginada"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Page<MovimientoDTO>> listarPorTipo(
            @Parameter(description = "Tipo de movimiento (ENTRADA / SALIDA)") @PathVariable TipoMovimiento tipo,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarPorTipo(tipo, pageable));
    }

    @GetMapping("/fechas")
    @Operation(summary = "Listar movimientos por rango de fechas",
               description = "Retorna movimientos dentro de un rango de fechas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de movimientos en el rango paginada"),
        @ApiResponse(responseCode = "400", description = "Fechas inválidas"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Page<MovimientoDTO>> listarPorFechas(
            @Parameter(description = "Fecha de inicio (ISO-8601)") @RequestParam LocalDateTime inicio,
            @Parameter(description = "Fecha de fin (ISO-8601)") @RequestParam LocalDateTime fin,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarPorFechas(inicio, fin, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar movimiento",
               description = "Registra una entrada o salida de inventario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Movimiento registrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<MovimientoDTO> registrar(
            @Valid @RequestBody MovimientoDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrar(dto, usuario.getId()));
    }
}
