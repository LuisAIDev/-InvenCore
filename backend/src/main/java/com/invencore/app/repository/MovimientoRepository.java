package com.invencore.app.repository;

import com.invencore.app.model.entity.Movimiento;
import com.invencore.app.model.entity.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByProductoId(Long productoId);
    Page<Movimiento> findByProductoId(Long productoId, Pageable pageable);
    List<Movimiento> findByTipo(TipoMovimiento tipo);
    Page<Movimiento> findByTipo(TipoMovimiento tipo, Pageable pageable);
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    Page<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    List<Movimiento> findByUsuarioId(Long usuarioId);
}
