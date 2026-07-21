package com.invencore.app.controller;

import com.invencore.app.model.dto.CategoriaPublicaDTO;
import com.invencore.app.model.dto.ProductoPublicoDTO;
import com.invencore.app.service.CategoriaService;
import com.invencore.app.service.ProductoService;
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
public class PublicoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/productos")
    public ResponseEntity<Page<ProductoPublicoDTO>> listarProductos(
            @RequestParam(required = false) Long categoriaId,
            Pageable pageable) {
        return ResponseEntity.ok(productoService.listarPublicos(categoriaId, pageable));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaPublicaDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarPublicas());
    }
}
