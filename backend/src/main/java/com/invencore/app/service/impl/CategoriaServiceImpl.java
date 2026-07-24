package com.invencore.app.service.impl;

import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.model.dto.CategoriaDTO;
import com.invencore.app.model.dto.CategoriaPublicaDTO;
import com.invencore.app.model.entity.Categoria;
import com.invencore.app.repository.CategoriaRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public Page<CategoriaDTO> listarTodos(Pageable pageable) {
        Page<Categoria> categorias = categoriaRepository.findAll(pageable);
        List<Long> ids = categorias.stream().map(Categoria::getId).toList();
        Map<Long, Long> counts = Map.of();
        if (!ids.isEmpty()) {
            List<Object[]> results = productoRepository.countProductosPorCategoriaIds(ids);
            counts = results.stream()
                    .collect(Collectors.toMap(
                            arr -> (Long) arr[0],
                            arr -> ((Number) arr[1]).longValue()
                    ));
        }
        return categorias.map(c -> toDTO(c, counts.getOrDefault(c.getId(), 0L)));
    }

    @Override
    public Page<CategoriaDTO> listarActivos(Pageable pageable) {
        Page<Categoria> categorias = categoriaRepository.findByActivoTrue(pageable);
        List<Long> ids = categorias.stream().map(Categoria::getId).toList();
        Map<Long, Long> counts = Map.of();
        if (!ids.isEmpty()) {
            List<Object[]> results = productoRepository.countProductosPorCategoriaIds(ids);
            counts = results.stream()
                    .collect(Collectors.toMap(
                            arr -> (Long) arr[0],
                            arr -> ((Number) arr[1]).longValue()
                    ));
        }
        return categorias.map(c -> toDTO(c, counts.getOrDefault(c.getId(), 0L)));
    }

    @Override
    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return toDTO(categoria);
    }

    @Override
    public CategoriaDTO crear(CategoriaDTO dto) {
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .activo(true)
                .build();
        return toDTO(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return toDTO(categoriaRepository.save(categoria));
    }

    @Override
    public List<CategoriaPublicaDTO> listarPublicas() {
        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(this::toPublicoDTO)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaPublicaDTO toPublicoDTO(Categoria c) {
        CategoriaPublicaDTO dto = new CategoriaPublicaDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        return dto;
    }

    private CategoriaDTO toDTO(Categoria c, long productCount) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setActivo(c.getActivo());
        dto.setProductCount(productCount);
        return dto;
    }

    private CategoriaDTO toDTO(Categoria c) {
        return toDTO(c, 0L);
    }
}
