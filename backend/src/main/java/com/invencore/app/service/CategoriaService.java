package com.invencore.app.service;

import com.invencore.app.model.dto.CategoriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriaService {
    Page<CategoriaDTO> listarTodos(Pageable pageable);
    Page<CategoriaDTO> listarActivos(Pageable pageable);
    CategoriaDTO buscarPorId(Long id);
    CategoriaDTO crear(CategoriaDTO dto);
    CategoriaDTO actualizar(Long id, CategoriaDTO dto);
    void eliminar(Long id);
}
