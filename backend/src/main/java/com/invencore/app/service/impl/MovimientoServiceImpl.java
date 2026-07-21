package com.invencore.app.service.impl;

import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.model.dto.MovimientoDTO;
import com.invencore.app.model.entity.Movimiento;
import com.invencore.app.model.entity.Producto;
import com.invencore.app.model.entity.TipoMovimiento;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.repository.MovimientoRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.repository.UsuarioRepository;
import com.invencore.app.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoServiceImpl.class);

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<MovimientoDTO> listarTodos() {
        return movimientoRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<MovimientoDTO> listarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoId(productoId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<MovimientoDTO> listarPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipo(tipo)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<MovimientoDTO> listarPorFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public MovimientoDTO registrar(MovimientoDTO dto, Long usuarioId) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (dto.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStock() < dto.getCantidad()) {
                log.warn("Stock insuficiente para salida: productoId={}, stockActual={}, cantidadSolicitada={}",
                        dto.getProductoId(), producto.getStock(), dto.getCantidad());
                throw new RuntimeException("Stock insuficiente. Stock actual: " + producto.getStock());
            }
            producto.setStock(producto.getStock() - dto.getCantidad());
        } else {
            producto.setStock(producto.getStock() + dto.getCantidad());
        }

        Movimiento movimiento = Movimiento.builder()
                .tipo(dto.getTipo())
                .cantidad(dto.getCantidad())
                .descripcion(dto.getDescripcion())
                .producto(producto)
                .usuario(usuario)
                .build();

        MovimientoDTO saved = toDTO(movimientoRepository.save(movimiento));
        log.info("Movimiento registrado: id={}, tipo={}, cantidad={}, productoId={}, usuarioId={}",
                saved.getId(), dto.getTipo(), dto.getCantidad(), dto.getProductoId(), usuarioId);
        return saved;
    }

    private MovimientoDTO toDTO(Movimiento m) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(m.getId());
        dto.setTipo(m.getTipo());
        dto.setCantidad(m.getCantidad());
        dto.setDescripcion(m.getDescripcion());
        dto.setFecha(m.getFecha());
        dto.setProductoId(m.getProducto().getId());
        dto.setProductoNombre(m.getProducto().getNombre());
        dto.setUsuarioId(m.getUsuario().getId());
        dto.setUsuarioNombre(m.getUsuario().getNombre());
        return dto;
    }
}
