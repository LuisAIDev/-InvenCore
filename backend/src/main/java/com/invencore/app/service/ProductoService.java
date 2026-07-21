package com.invencore.app.service;

import com.invencore.app.model.dto.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    Page<ProductoDTO> listarTodos(Pageable pageable);
    Page<ProductoDTO> listarActivos(Pageable pageable);
    Page<ProductoDTO> listarConStockBajo(Pageable pageable);
    ProductoDTO buscarPorId(Long id);
    ProductoDTO crear(ProductoDTO dto);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    void eliminar(Long id);
}
