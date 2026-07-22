package com.invencore.app.controller;

import com.invencore.app.model.dto.CategoriaPublicaDTO;
import com.invencore.app.model.dto.ProductoPublicoDTO;
import com.invencore.app.service.CategoriaService;
import com.invencore.app.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
@Tag(name = "Public Catalog", description = "Catálogo público (sin autenticación)")
public class PublicoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/productos")
    @Operation(summary = "Listar productos del catálogo público",
               description = "Retorna productos activos y disponibles paginados")
    public ResponseEntity<Page<ProductoPublicoDTO>> listarProductos(
            @Parameter(description = "Filtrar por ID de categoría") @RequestParam(required = false) Long categoriaId,
            Pageable pageable) {
        return ResponseEntity.ok(productoService.listarPublicos(categoriaId, pageable));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías del catálogo público",
               description = "Retorna categorías activas")
    public ResponseEntity<List<CategoriaPublicaDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarPublicas());
    }
}
