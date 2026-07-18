package com.invencore.app.service;

import com.invencore.app.model.dto.MovimientoDTO;
import com.invencore.app.model.entity.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {
    List<MovimientoDTO> listarTodos();
    List<MovimientoDTO> listarPorProducto(Long productoId);
    List<MovimientoDTO> listarPorTipo(TipoMovimiento tipo);
    List<MovimientoDTO> listarPorFechas(LocalDateTime inicio, LocalDateTime fin);
    MovimientoDTO registrar(MovimientoDTO dto, Long usuarioId);
}
