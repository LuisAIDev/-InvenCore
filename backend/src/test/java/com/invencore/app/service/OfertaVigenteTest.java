package com.invencore.app.service;

import com.invencore.app.model.entity.Oferta;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.repository.OfertaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OfertaVigenteTest {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private TestEntityManager em;

    private Producto crearProducto() {
        Producto p = Producto.builder()
                .nombre("Producto Test")
                .precio(BigDecimal.valueOf(100))
                .stock(10)
                .activo(true)
                .build();
        return em.persistAndFlush(p);
    }

    @Test
    void ofertaVigente_debeSerEncontrada() {
        Producto p = crearProducto();
        Oferta oferta = Oferta.builder()
                .nombre("Vigente")
                .porcentajeDescuento(BigDecimal.valueOf(10))
                .fechaInicio(LocalDateTime.now().minusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1))
                .activa(true)
                .productos(Set.of(p))
                .build();
        em.persistAndFlush(oferta);

        var resultado = ofertaRepository.findActiveOffersForProduct(p.getId(), LocalDateTime.now());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Vigente");
    }

    @Test
    void ofertaConFechaFinPasada_noDebeSerEncontrada() {
        Producto p = crearProducto();
        Oferta oferta = Oferta.builder()
                .nombre("Vencida")
                .porcentajeDescuento(BigDecimal.valueOf(10))
                .fechaInicio(LocalDateTime.now().minusDays(10))
                .fechaFin(LocalDateTime.now().minusDays(1))
                .activa(true)
                .productos(Set.of(p))
                .build();
        em.persistAndFlush(oferta);

        var resultado = ofertaRepository.findActiveOffersForProduct(p.getId(), LocalDateTime.now());

        assertThat(resultado).isEmpty();
    }

    @Test
    void ofertaInactiva_noDebeSerEncontrada() {
        Producto p = crearProducto();
        Oferta oferta = Oferta.builder()
                .nombre("Inactiva")
                .porcentajeDescuento(BigDecimal.valueOf(10))
                .fechaInicio(LocalDateTime.now().minusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1))
                .activa(false)
                .productos(Set.of(p))
                .build();
        em.persistAndFlush(oferta);

        var resultado = ofertaRepository.findActiveOffersForProduct(p.getId(), LocalDateTime.now());

        assertThat(resultado).isEmpty();
    }
}
