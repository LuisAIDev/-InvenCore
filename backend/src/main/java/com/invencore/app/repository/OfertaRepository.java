package com.invencore.app.repository;

import com.invencore.app.model.entity.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    @Query("SELECT o FROM Oferta o JOIN o.productos p WHERE p.id = :productoId AND o.activa = true AND o.fechaInicio <= :ahora AND o.fechaFin >= :ahora")
    List<Oferta> findActiveOffersForProduct(@Param("productoId") Long productoId, @Param("ahora") LocalDateTime ahora);

    @Query("SELECT DISTINCT o FROM Oferta o JOIN FETCH o.productos p WHERE p.id IN :productoIds AND o.activa = true AND o.fechaInicio <= :ahora AND o.fechaFin >= :ahora")
    List<Oferta> findActiveOffersForProducts(@Param("productoIds") List<Long> productoIds, @Param("ahora") LocalDateTime ahora);
}
