package com.invencore.app.service;

import com.invencore.app.model.dto.CategoriaDTO;

import java.util.List;

public interface CategoriaService {
    List<CategoriaDTO> listarTodos();
    List<CategoriaDTO> listarActivos();
    CategoriaDTO buscarPorId(Long id);
    CategoriaDTO crear(CategoriaDTO dto);
    CategoriaDTO actualizar(Long id, CategoriaDTO dto);
    void eliminar(Long id);
}
