package com.invencore.app.service.impl;

import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.model.dto.ProductoDTO;
import com.invencore.app.model.dto.ProductoPublicoDTO;
import com.invencore.app.model.entity.Categoria;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.repository.CategoriaRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public Page<ProductoDTO> listarTodos(Pageable pageable) {
        return productoRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public Page<ProductoDTO> listarActivos(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable).map(this::toDTO);
    }

    @Override
    public Page<ProductoPublicoDTO> listarPublicos(Long categoriaId, Pageable pageable) {
        Page<Producto> productos;
        if (categoriaId != null) {
            productos = productoRepository.findByActivoTrueAndStockGreaterThanAndCategoriaId(0, categoriaId, pageable);
        } else {
            productos = productoRepository.findByActivoTrueAndStockGreaterThan(0, pageable);
        }
        return productos.map(this::toPublicoDTO);
    }

    @Override
    public Page<ProductoDTO> listarConStockBajo(Pageable pageable) {
        return productoRepository.findProductosConStockBajo(pageable).map(this::toDTO);
    }

    @Override
    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return toDTO(producto);
    }

    @Override
    public ProductoDTO crear(ProductoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
                .stockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5)
                .activo(true)
                .categoria(categoria)
                .build();
        ProductoDTO saved = toDTO(productoRepository.save(producto));
        log.info("Producto creado: id={}, nombre='{}', categoriaId={}", saved.getId(), saved.getNombre(), dto.getCategoriaId());
        return saved;
    }

    @Override
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setActivo(dto.getActivo());
        producto.setCategoria(categoria);
        ProductoDTO updated = toDTO(productoRepository.save(producto));
        log.info("Producto actualizado: id={}, nombre='{}'", id, updated.getNombre());
        return updated;
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoPublicoDTO toPublicoDTO(Producto p) {
        ProductoPublicoDTO dto = new ProductoPublicoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setDisponible(p.getStock() > 0);
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNombre(p.getCategoria().getNombre());
        }
        return dto;
    }

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setStock(p.getStock());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setActivo(p.getActivo());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNombre(p.getCategoria().getNombre());
        }
        return dto;
    }
}
