package com.invencore.app.controller;

import com.invencore.app.model.dto.CategoriaDTO;
import com.invencore.app.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Gestión de categorías")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar categorías activas",
               description = "Retorna categorías activas paginadas (público)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de categorías activas paginada")
    })
    public ResponseEntity<Page<CategoriaDTO>> listarActivos(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listarActivos(pageable));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las categorías",
               description = "Retorna todas las categorías incluyendo inactivas (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista completa de categorías paginada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<Page<CategoriaDTO>> listarTodos(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoría por ID", description = "Retorna una categoría por su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada",
            content = @Content(schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaDTO> buscarPorId(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaDTO> actualizar(
            @Parameter(description = "ID de la categoría") @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría por ID (solo ADMIN)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
