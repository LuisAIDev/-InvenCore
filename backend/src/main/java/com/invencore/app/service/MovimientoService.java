package com.invencore.app.service;

import com.invencore.app.model.dto.MovimientoDTO;
import com.invencore.app.model.entity.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface MovimientoService {
    Page<MovimientoDTO> listarTodos(Pageable pageable);
    Page<MovimientoDTO> listarPorProducto(Long productoId, Pageable pageable);
    Page<MovimientoDTO> listarPorTipo(TipoMovimiento tipo, Pageable pageable);
    Page<MovimientoDTO> listarPorFechas(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    MovimientoDTO registrar(MovimientoDTO dto, Long usuarioId);
}
