package com.invencore.app.controller;

import com.invencore.app.model.dto.OfertaDTO;
import com.invencore.app.service.OfertaService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Offers", description = "Gestión de ofertas (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class OfertaController {

    private final OfertaService ofertaService;

    @GetMapping
    @Operation(summary = "Listar ofertas", description = "Retorna todas las ofertas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ofertas"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<List<OfertaDTO>> listar() {
        return ResponseEntity.ok(ofertaService.listarTodos());
    }

    @GetMapping("/pagina")
    @Operation(summary = "Listar ofertas paginadas", description = "Retorna ofertas paginadas (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de ofertas"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<Page<OfertaDTO>> listarPaginado(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ofertaService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar oferta por ID", description = "Retorna una oferta por su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Oferta encontrada",
            content = @Content(schema = @Schema(implementation = OfertaDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    public ResponseEntity<OfertaDTO> buscarPorId(
            @Parameter(description = "ID de la oferta") @PathVariable Long id) {
        return ResponseEntity.ok(ofertaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear oferta", description = "Crea una nueva oferta con productos asociados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Oferta creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<OfertaDTO> crear(@Valid @RequestBody OfertaDTO dto) {
        return ResponseEntity.ok(ofertaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar oferta", description = "Actualiza una oferta existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Oferta actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    public ResponseEntity<OfertaDTO> actualizar(
            @Parameter(description = "ID de la oferta") @PathVariable Long id,
            @Valid @RequestBody OfertaDTO dto) {
        return ResponseEntity.ok(ofertaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar oferta", description = "Elimina una oferta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Oferta eliminada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la oferta") @PathVariable Long id) {
        ofertaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
