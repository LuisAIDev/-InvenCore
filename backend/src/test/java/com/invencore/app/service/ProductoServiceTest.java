package com.invencore.app.service;

import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.model.dto.ProductoDTO;
import com.invencore.app.model.entity.Categoria;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.repository.CategoriaRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoService = new ProductoServiceImpl(productoRepository, categoriaRepository);
    }

    @Test
    void crear_debePersistirProductoYRetornarDTO() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónicos");

        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Laptop");
        dto.setDescripcion("Laptop gamer");
        dto.setPrecio(BigDecimal.valueOf(15000));
        dto.setStock(10);
        dto.setStockMinimo(3);
        dto.setCategoriaId(1L);

        Producto entity = Producto.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop gamer")
                .precio(BigDecimal.valueOf(15000))
                .stock(10)
                .stockMinimo(3)
                .activo(true)
                .categoria(categoria)
                .build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(entity);

        ProductoDTO resultado = productoService.crear(dto);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Laptop");
        assertThat(resultado.getPrecio()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        assertThat(resultado.getStock()).isEqualTo(10);
        assertThat(resultado.getCategoriaId()).isEqualTo(1L);
        assertThat(resultado.getCategoriaNombre()).isEqualTo("Electrónicos");

        verify(productoRepository).save(productoCaptor.capture());
        Producto capturado = productoCaptor.getValue();
        assertThat(capturado.getNombre()).isEqualTo("Laptop");
        assertThat(capturado.getActivo()).isTrue();
    }

    @Test
    void listarActivos_debeRetornarPaginaDeProductosActivos() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónicos");

        Producto p1 = Producto.builder().id(1L).nombre("Laptop").precio(BigDecimal.valueOf(15000)).stock(10).stockMinimo(3).activo(true).categoria(categoria).build();
        Producto p2 = Producto.builder().id(2L).nombre("Mouse").precio(BigDecimal.valueOf(500)).stock(20).stockMinimo(5).activo(true).categoria(categoria).build();

        Page<Producto> page = new PageImpl<>(List.of(p1, p2));
        when(productoRepository.findByActivoTrue(any(Pageable.class))).thenReturn(page);

        Page<ProductoDTO> resultados = productoService.listarActivos(Pageable.ofSize(10));

        assertThat(resultados).hasSize(2);
        assertThat(resultados.getContent().get(0).getNombre()).isEqualTo("Laptop");
        assertThat(resultados.getContent().get(1).getNombre()).isEqualTo("Mouse");
    }

    @Test
    void actualizar_debeModificarCamposPermitidosYRetornarDTO() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónicos");

        Categoria categoriaNueva = new Categoria();
        categoriaNueva.setId(2L);
        categoriaNueva.setNombre("Computación");

        Producto existente = Producto.builder()
                .id(1L).nombre("Laptop").descripcion("Vieja").precio(BigDecimal.valueOf(10000))
                .stock(10).stockMinimo(3).activo(true).categoria(categoria)
                .build();

        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Laptop Pro");
        dto.setDescripcion("Actualizada");
        dto.setPrecio(BigDecimal.valueOf(18000));
        dto.setStockMinimo(2);
        dto.setActivo(true);
        dto.setCategoriaId(2L);

        Producto actualizado = Producto.builder()
                .id(1L).nombre("Laptop Pro").descripcion("Actualizada").precio(BigDecimal.valueOf(18000))
                .stock(10).stockMinimo(2).activo(true).categoria(categoriaNueva)
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoriaNueva));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        ProductoDTO resultado = productoService.actualizar(1L, dto);

        assertThat(resultado.getNombre()).isEqualTo("Laptop Pro");
        assertThat(resultado.getPrecio()).isEqualByComparingTo(BigDecimal.valueOf(18000));
        assertThat(resultado.getStockMinimo()).isEqualTo(2);
        assertThat(resultado.getCategoriaId()).isEqualTo(2L);
    }

    @Test
    void buscarPorId_conIdInexistente_debeLanzarResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
