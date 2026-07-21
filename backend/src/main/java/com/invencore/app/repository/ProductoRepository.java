package com.invencore.app.repository;

import com.invencore.app.model.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    Page<Producto> findByActivoTrue(Pageable pageable);
    List<Producto> findByCategoriaId(Long categoriaId);
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    Page<Producto> findProductosConStockBajo(Pageable pageable);
    boolean existsByNombre(String nombre);
}
