package com.invencore.app.service;

import com.invencore.app.model.dto.ProductoDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoDTO> listarTodos();
    List<ProductoDTO> listarActivos();
    List<ProductoDTO> listarConStockBajo();
    ProductoDTO buscarPorId(Long id);
    ProductoDTO crear(ProductoDTO dto);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    void eliminar(Long id);
}
