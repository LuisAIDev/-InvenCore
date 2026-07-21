package com.invencore.app.service;

import com.invencore.app.model.dto.OfertaDTO;
import com.invencore.app.model.entity.Categoria;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.repository.CategoriaRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.repository.OfertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OfertaCreacionIntegrationTest {

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Producto p1;
    private Producto p2;

    @BeforeEach
    void setUp() {
        ofertaRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
        Categoria cat = Categoria.builder().nombre("Test-Cat").activo(true).build();
        cat = categoriaRepository.save(cat);

        p1 = productoRepository.save(Producto.builder()
                .nombre("Producto A").precio(BigDecimal.valueOf(100)).stock(10).activo(true).categoria(cat).build());
        p2 = productoRepository.save(Producto.builder()
                .nombre("Producto B").precio(BigDecimal.valueOf(200)).stock(5).activo(true).categoria(cat).build());
    }

    @Test
    void crearOfertaConProductos_debePersistirJoinTable() {
        OfertaDTO dto = new OfertaDTO();
        dto.setNombre("Integración Test");
        dto.setPorcentajeDescuento(BigDecimal.valueOf(20));
        dto.setFechaInicio(LocalDateTime.now().minusDays(1));
        dto.setFechaFin(LocalDateTime.now().plusDays(1));
        dto.setActiva(true);
        dto.setProductoIds(List.of(p1.getId(), p2.getId()));

        OfertaDTO result = ofertaService.crear(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Integración Test");
        assertThat(result.getProductoIds()).isNotNull();
        assertThat(result.getProductoIds()).hasSize(2);
        assertThat(result.getProductoIds()).containsExactlyInAnyOrder(p1.getId(), p2.getId());
    }

    @Test
    void crearOfertaSinProductos_debeRetornarListaVacia() {
        OfertaDTO dto = new OfertaDTO();
        dto.setNombre("Sin productos");
        dto.setPorcentajeDescuento(BigDecimal.valueOf(10));
        dto.setFechaInicio(LocalDateTime.now().minusDays(1));
        dto.setFechaFin(LocalDateTime.now().plusDays(1));
        dto.setActiva(true);

        OfertaDTO result = ofertaService.crear(dto);

        assertThat(result.getProductoIds()).isNullOrEmpty();
    }

    @Test
    void listarOfertas_debeIncluirProductos() {
        OfertaDTO dto = new OfertaDTO();
        dto.setNombre("Para listar");
        dto.setPorcentajeDescuento(BigDecimal.valueOf(15));
        dto.setFechaInicio(LocalDateTime.now().minusDays(1));
        dto.setFechaFin(LocalDateTime.now().plusDays(1));
        dto.setActiva(true);
        dto.setProductoIds(List.of(p1.getId()));

        ofertaService.crear(dto);

        List<OfertaDTO> todas = ofertaService.listarTodos();
        assertThat(todas).hasSize(1);
        assertThat(todas.get(0).getProductoIds()).containsExactly(p1.getId());
    }
}
