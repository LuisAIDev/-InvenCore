package com.invencore.app.repository;

import com.invencore.app.model.entity.Movimiento;
import com.invencore.app.model.entity.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByProductoId(Long productoId);
    List<Movimiento> findByTipo(TipoMovimiento tipo);
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Movimiento> findByUsuarioId(Long usuarioId);
}
