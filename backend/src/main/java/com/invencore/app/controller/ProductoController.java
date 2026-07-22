package com.invencore.app.controller;

import com.invencore.app.model.dto.ProductoDTO;
import com.invencore.app.service.ProductoService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos activos",
               description = "Retorna productos activos paginados (público)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos activos paginada")
    })
    public ResponseEntity<Page<ProductoDTO>> listarActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.listarActivos(pageable));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los productos",
               description = "Retorna todos los productos incluyendo inactivos (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista completa de productos paginada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<Page<ProductoDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.listarTodos(pageable));
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Listar productos con stock bajo",
               description = "Retorna productos activos cuyo stock está por debajo del mínimo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo paginada")
    })
    public ResponseEntity<Page<ProductoDTO>> listarConStockBajo(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.listarConStockBajo(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Retorna un producto por su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO> buscarPorId(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO> actualizar(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por ID (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
