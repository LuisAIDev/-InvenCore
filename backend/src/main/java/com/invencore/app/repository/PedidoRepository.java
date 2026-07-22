package com.invencore.app.repository;

import com.invencore.app.model.entity.EstadoPedido;
import com.invencore.app.model.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.producto LEFT JOIN FETCH p.pago ORDER BY p.fechaCreacion DESC")
    java.util.List<Pedido> findAllWithItems();

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.producto LEFT JOIN FETCH p.pago WHERE p.id = :id")
    Optional<Pedido> findByIdWithItems(@Param("id") Long id);

    @EntityGraph(attributePaths = {"items", "items.producto", "pago"})
    Page<Pedido> findAllByOrderByFechaCreacionDesc(Pageable pageable);
}
