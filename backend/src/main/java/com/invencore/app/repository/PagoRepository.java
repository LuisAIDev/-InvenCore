package com.invencore.app.repository;

import com.invencore.app.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPaymentIntentId(String paymentIntentId);

    @Query("SELECT p FROM Pago p JOIN FETCH p.pedido ped LEFT JOIN FETCH ped.items i LEFT JOIN FETCH i.producto WHERE p.paymentIntentId = :pid")
    Optional<Pago> findByPaymentIntentIdWithPedido(@Param("pid") String paymentIntentId);
}
