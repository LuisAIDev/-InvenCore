package com.invencore.app.service;

import com.invencore.app.model.dto.OfertaDTO;

import java.util.List;

public interface OfertaService {
    List<OfertaDTO> listarTodos();
    OfertaDTO buscarPorId(Long id);
    OfertaDTO crear(OfertaDTO dto);
    OfertaDTO actualizar(Long id, OfertaDTO dto);
    void eliminar(Long id);
}
