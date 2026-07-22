package com.invencore.app.service.impl;

import com.invencore.app.exception.PagoException;
import com.invencore.app.exception.PedidoNoValidoException;
import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.exception.StockInsuficienteException;
import com.invencore.app.model.dto.PagoDTO;
import com.invencore.app.model.dto.PedidoDTO;
import com.invencore.app.model.dto.PedidoItemDTO;
import com.invencore.app.model.dto.PedidoRequestDTO;
import com.invencore.app.model.entity.*;
import com.invencore.app.repository.OfertaRepository;
import com.invencore.app.repository.PagoRepository;
import com.invencore.app.repository.PedidoRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.PedidoService;
import com.invencore.app.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final ProductoRepository productoRepository;
    private final OfertaRepository ofertaRepository;
    private final StripeService stripeService;

    @Override
    @Transactional
    public PedidoDTO crearPedido(PedidoRequestDTO request) {
        Pedido pedido = Pedido.builder()
                .clienteNombre(request.getClienteNombre())
                .clienteEmail(request.getClienteEmail())
                .build();

        for (PedidoRequestDTO.ItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findByIdForUpdate(itemReq.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado: " + itemReq.getProductoId()));

            if (!producto.getActivo()) {
                throw new PedidoNoValidoException("Producto no disponible: " + producto.getNombre());
            }
            if (producto.getStock() < itemReq.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para: " + producto.getNombre());
            }

            BigDecimal descuento = BigDecimal.ZERO;
            List<Oferta> ofertasProducto = ofertaRepository.findActiveOffersForProduct(
                    producto.getId(), LocalDateTime.now());
            if (!ofertasProducto.isEmpty()) {
                descuento = ofertasProducto.get(0).getPorcentajeDescuento();
            }

            BigDecimal precioBase = producto.getPrecio();
            BigDecimal precioConDescuento = precioBase.multiply(
                    BigDecimal.ONE.subtract(descuento.divide(
                            new BigDecimal("100"), 4, RoundingMode.HALF_UP)));
            BigDecimal subtotal = precioConDescuento.multiply(BigDecimal.valueOf(itemReq.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            PedidoItem item = PedidoItem.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(itemReq.getCantidad())
                    .precioUnitario(precioConDescuento.setScale(2, RoundingMode.HALF_UP))
                    .subtotal(subtotal)
                    .build();
            pedido.getItems().add(item);
        }

        BigDecimal total = pedido.getItems().stream()
                .map(PedidoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        pedido.setTotal(total);

        pedido = pedidoRepository.save(pedido);

        try {
            Long montoCentavos = total.multiply(new BigDecimal("100")).longValue();
            montoCentavos = montoCentavos > 50 ? montoCentavos : 50;

            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    montoCentavos, "mxn",
                    "Pedido #" + pedido.getId());

            Pago pago = Pago.builder()
                    .pedido(pedido)
                    .paymentIntentId(paymentIntent.getId())
                    .estado(EstadoPago.PENDIENTE)
                    .monto(total)
                    .stripeResponseJson(paymentIntent.toJson())
                    .build();
            pago = pagoRepository.save(pago);
            pedido.setPago(pago);
            pedidoRepository.save(pedido);

            PedidoDTO dto = toDTO(pedido);
            dto.setClientSecret(paymentIntent.getClientSecret());
            dto.setTokenConfirmacion(pedido.getTokenConfirmacion());
            return dto;

        } catch (StripeException e) {
            throw new PagoException("Error al procesar el pago con Stripe", e);
        }
    }

    @Override
    @Transactional
    public PedidoDTO confirmarPago(Long pedidoId, String token) {
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + pedidoId));

        if (!pedido.getTokenConfirmacion().equals(token)) {
            throw new PedidoNoValidoException("Token de confirmación inválido");
        }

        return ejecutarConfirmacion(pedido);
    }

    @Override
    @Transactional
    public PedidoDTO confirmarPagoWebhook(String paymentIntentId) {
        Pago pago = pagoRepository.findByPaymentIntentIdWithPedido(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pago no encontrado: " + paymentIntentId));
        return ejecutarConfirmacion(pago.getPedido());
    }

    private PedidoDTO ejecutarConfirmacion(Pedido pedido) {
        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            return toDTO(pedido);
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new PedidoNoValidoException("El pedido no está pendiente");
        }

        if (pedido.getPago() == null) {
            throw new PedidoNoValidoException("El pedido no tiene un pago asociado");
        }

        try {
            PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(
                    pedido.getPago().getPaymentIntentId());

            switch (paymentIntent.getStatus()) {
                case "succeeded" -> {
                    pedido.setEstado(EstadoPedido.PAGADO);
                    pedido.getPago().setEstado(EstadoPago.COMPLETADO);
                    pedido.getPago().setStripeResponseJson(paymentIntent.toJson());
                    for (PedidoItem item : pedido.getItems()) {
                        Producto prod = productoRepository.findByIdForUpdate(item.getProducto().getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Producto no encontrado: " + item.getProducto().getId()));
                        prod.setStock(prod.getStock() - item.getCantidad());
                        productoRepository.save(prod);
                    }
                }
                case "canceled" -> {
                    pedido.setEstado(EstadoPedido.CANCELADO);
                    pedido.getPago().setEstado(EstadoPago.FALLIDO);
                }
                default -> throw new PagoException(
                        "Pago no completado. Estado: " + paymentIntent.getStatus());
            }
            pedidoRepository.save(pedido);
            return toDTO(pedido);

        } catch (StripeException e) {
            throw new PagoException("Error al verificar el pago con Stripe", e);
        }
    }

    @Override
    @Transactional
    public PedidoDTO cancelarPedido(Long pedidoId, String token) {
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + pedidoId));

        if (token != null && !pedido.getTokenConfirmacion().equals(token)) {
            throw new PedidoNoValidoException("Token de confirmación inválido");
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new PedidoNoValidoException("Solo se pueden cancelar pedidos pendientes");
        }

        if (pedido.getPago() != null && pedido.getPago().getPaymentIntentId() != null) {
            try {
                PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(
                        pedido.getPago().getPaymentIntentId());
                if ("requires_payment_method".equals(paymentIntent.getStatus())
                        || "requires_confirmation".equals(paymentIntent.getStatus())) {
                    paymentIntent.cancel();
                }
                pedido.getPago().setEstado(EstadoPago.FALLIDO);
            } catch (StripeException e) {
                throw new PagoException("Error al cancelar el pago en Stripe", e);
            }
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
        return toDTO(pedido);
    }

    @Override
    public PedidoDTO obtenerPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + pedidoId));
        PedidoDTO dto = toDTO(pedido);
        dto.setClientSecret(null);
        return dto;
    }

    @Override
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAllWithItems().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Page<PedidoDTO> listarTodos(Pageable pageable) {
        return pedidoRepository.findAllByOrderByFechaCreacionDesc(pageable)
                .map(this::toDTO);
    }

    private PedidoDTO toDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setClienteNombre(pedido.getClienteNombre());
        dto.setClienteEmail(pedido.getClienteEmail());
        dto.setEstado(pedido.getEstado().name());
        dto.setTotal(pedido.getTotal());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        dto.setTokenConfirmacion(null);

        dto.setItems(pedido.getItems().stream().map(this::toItemDTO).toList());

        if (pedido.getPago() != null) {
            PagoDTO pagoDTO = new PagoDTO();
            pagoDTO.setId(pedido.getPago().getId());
            pagoDTO.setPaymentIntentId(pedido.getPago().getPaymentIntentId());
            pagoDTO.setEstado(pedido.getPago().getEstado().name());
            pagoDTO.setMonto(pedido.getPago().getMonto());
            pagoDTO.setFechaCreacion(pedido.getPago().getFechaCreacion());
            dto.setPago(pagoDTO);
        }

        return dto;
    }

    private PedidoItemDTO toItemDTO(PedidoItem item) {
        PedidoItemDTO dto = new PedidoItemDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProducto().getId());
        dto.setProductoNombre(item.getProducto().getNombre());
        dto.setProductoImagenUrl(item.getProducto().getImagenUrl());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());

        BigDecimal precioOriginal = item.getProducto().getPrecio();
        if (precioOriginal.compareTo(item.getPrecioUnitario()) > 0) {
            BigDecimal diff = precioOriginal.subtract(item.getPrecioUnitario());
            BigDecimal pct = diff.multiply(new BigDecimal("100"))
                    .divide(precioOriginal, 0, RoundingMode.HALF_UP);
            dto.setPorcentajeDescuento(pct);
        } else {
            dto.setPorcentajeDescuento(BigDecimal.ZERO);
        }
        return dto;
    }
}
