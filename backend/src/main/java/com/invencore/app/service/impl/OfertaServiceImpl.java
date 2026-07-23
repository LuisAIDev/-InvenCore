package com.invencore.app.service.impl;

import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.model.dto.OfertaDTO;
import com.invencore.app.model.entity.Oferta;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.repository.OfertaRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.OfertaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OfertaServiceImpl implements OfertaService {

    private static final Logger log = LoggerFactory.getLogger(OfertaServiceImpl.class);

    private final OfertaRepository ofertaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OfertaDTO> listarTodos() {
        return ofertaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OfertaDTO> listarTodos(Pageable pageable) {
        return ofertaRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OfertaDTO buscarPorId(Long id) {
        return toDTO(ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada con id: " + id)));
    }

    @Override
    public OfertaDTO crear(OfertaDTO dto) {
        Oferta oferta = Oferta.builder()
                .nombre(dto.getNombre())
                .porcentajeDescuento(dto.getPorcentajeDescuento())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();
        if (dto.getProductoIds() != null) {
            List<Producto> productos = productoRepository.findAllById(dto.getProductoIds());
            oferta.getProductos().clear();
            productos.forEach(oferta.getProductos()::add);
        }
        OfertaDTO saved = toDTO(ofertaRepository.save(oferta));
        log.info("Oferta creada: id={}, nombre='{}', descuento={}%", saved.getId(), saved.getNombre(), saved.getPorcentajeDescuento());
        return saved;
    }

    @Override
    public OfertaDTO actualizar(Long id, OfertaDTO dto) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada con id: " + id));
        oferta.setNombre(dto.getNombre());
        oferta.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        oferta.setFechaInicio(dto.getFechaInicio());
        oferta.setFechaFin(dto.getFechaFin());
        oferta.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        if (dto.getProductoIds() != null) {
            List<Producto> productos = productoRepository.findAllById(dto.getProductoIds());
            oferta.getProductos().clear();
            productos.forEach(oferta.getProductos()::add);
        }
        OfertaDTO updated = toDTO(ofertaRepository.save(oferta));
        log.info("Oferta actualizada: id={}, nombre='{}'", id, updated.getNombre());
        return updated;
    }

    @Override
    public void eliminar(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta no encontrada con id: " + id));
        ofertaRepository.delete(oferta);
        log.info("Oferta eliminada: id={}, nombre='{}'", id, oferta.getNombre());
    }

    private OfertaDTO toDTO(Oferta o) {
        OfertaDTO dto = new OfertaDTO();
        dto.setId(o.getId());
        dto.setNombre(o.getNombre());
        dto.setPorcentajeDescuento(o.getPorcentajeDescuento());
        dto.setFechaInicio(o.getFechaInicio());
        dto.setFechaFin(o.getFechaFin());
        dto.setActiva(o.getActiva());
        if (o.getProductos() != null && !o.getProductos().isEmpty()) {
            dto.setProductoIds(o.getProductos().stream().map(Producto::getId).toList());
        }
        return dto;
    }
}
