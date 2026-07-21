package com.invencore.app.service;

import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.model.dto.OfertaDTO;
import com.invencore.app.model.entity.Oferta;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.repository.OfertaRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.impl.OfertaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfertaServiceTest {

    @Mock
    private OfertaRepository ofertaRepository;

    @Mock
    private ProductoRepository productoRepository;

    private OfertaService ofertaService;

    @BeforeEach
    void setUp() {
        ofertaService = new OfertaServiceImpl(ofertaRepository, productoRepository);
    }

    @Test
    void crear_debePersistirOfertaYRetornarDTO() {
        OfertaDTO dto = new OfertaDTO();
        dto.setNombre("Black Friday");
        dto.setPorcentajeDescuento(BigDecimal.valueOf(15.00));
        dto.setFechaInicio(LocalDateTime.of(2026, 7, 1, 0, 0));
        dto.setFechaFin(LocalDateTime.of(2026, 7, 31, 23, 59));
        dto.setProductoIds(List.of(1L, 2L));

        Producto p1 = new Producto();
        p1.setId(1L);
        Producto p2 = new Producto();
        p2.setId(2L);

        Oferta entity = Oferta.builder()
                .id(1L)
                .nombre("Black Friday")
                .porcentajeDescuento(BigDecimal.valueOf(15.00))
                .fechaInicio(LocalDateTime.of(2026, 7, 1, 0, 0))
                .fechaFin(LocalDateTime.of(2026, 7, 31, 23, 59))
                .activa(true)
                .productos(Set.of(p1, p2))
                .build();

        when(productoRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(p1, p2));
        when(ofertaRepository.save(any(Oferta.class))).thenReturn(entity);

        OfertaDTO resultado = ofertaService.crear(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Black Friday");
        assertThat(resultado.getPorcentajeDescuento()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
        assertThat(resultado.getProductoIds()).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void buscarPorId_conIdInexistente_debeLanzarResourceNotFoundException() {
        when(ofertaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ofertaService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void eliminar_debeRemoverOferta() {
        Oferta oferta = Oferta.builder().id(1L).nombre("Test").build();
        when(ofertaRepository.findById(1L)).thenReturn(Optional.of(oferta));

        ofertaService.eliminar(1L);

        verify(ofertaRepository).delete(oferta);
    }
}
