package com.invencore.app.service;

import com.invencore.app.exception.PedidoNoValidoException;
import com.invencore.app.exception.ResourceNotFoundException;
import com.invencore.app.exception.StockInsuficienteException;
import com.invencore.app.model.dto.PedidoDTO;
import com.invencore.app.model.dto.PedidoRequestDTO;
import com.invencore.app.model.entity.*;
import com.invencore.app.repository.OfertaRepository;
import com.invencore.app.repository.PagoRepository;
import com.invencore.app.repository.PedidoRepository;
import com.invencore.app.repository.ProductoRepository;
import com.invencore.app.service.impl.PedidoServiceImpl;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private OfertaRepository ofertaRepository;

    @Mock
    private StripeService stripeService;

    private PedidoService pedidoService;

    private static final String TOKEN = "test-token-uuid";

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoServiceImpl(pedidoRepository, pagoRepository, productoRepository, ofertaRepository, stripeService);
    }

    @Test
    void crearPedido_debeCalcularPrecioConDescuentoYRetornarClientSecret() throws Exception {
        Producto producto = Producto.builder()
                .id(1L).nombre("Laptop").precio(BigDecimal.valueOf(10000))
                .stock(10).stockMinimo(3).activo(true)
                .build();

        Oferta oferta = Oferta.builder()
                .id(1L).nombre("Verano").porcentajeDescuento(BigDecimal.valueOf(10))
                .fechaInicio(LocalDateTime.now().minusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1))
                .activa(true)
                .build();

        Pedido pedidoGuardado = Pedido.builder()
                .id(1L).clienteNombre("Juan").clienteEmail("juan@test.com")
                .total(BigDecimal.valueOf(18000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Pago pago = Pago.builder()
                .id(1L).pedido(pedidoGuardado)
                .paymentIntentId("pi_test_123").estado(EstadoPago.PENDIENTE)
                .monto(BigDecimal.valueOf(18000))
                .build();
        pedidoGuardado.setPago(pago);

        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getId()).thenReturn("pi_test_123");
        when(mockPi.getClientSecret()).thenReturn("secret_test_456");
        when(mockPi.toJson()).thenReturn("{\"id\":\"pi_test_123\"}");

        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));
        when(ofertaRepository.findActiveOffersForProduct(1L, LocalDateTime.now())).thenReturn(List.of(oferta));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(stripeService.createPaymentIntent(anyLong(), anyString(), anyString())).thenReturn(mockPi);
        when(pedidoRepository.save(pedidoGuardado)).thenReturn(pedidoGuardado);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setClienteNombre("Juan");
        request.setClienteEmail("juan@test.com");
        PedidoRequestDTO.ItemRequest item = new PedidoRequestDTO.ItemRequest();
        item.setProductoId(1L);
        item.setCantidad(2);
        request.setItems(List.of(item));

        PedidoDTO resultado = pedidoService.crearPedido(request);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getClienteNombre()).isEqualTo("Juan");
        assertThat(resultado.getClientSecret()).isEqualTo("secret_test_456");
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");

        verify(stripeService).createPaymentIntent(anyLong(), eq("mxn"), anyString());
    }

    @Test
    void crearPedido_conProductoInactivo_debeLanzarExcepcion() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Laptop").precio(BigDecimal.valueOf(10000))
                .stock(10).stockMinimo(3).activo(false)
                .build();

        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setClienteNombre("Juan");
        request.setClienteEmail("juan@test.com");
        PedidoRequestDTO.ItemRequest item = new PedidoRequestDTO.ItemRequest();
        item.setProductoId(1L);
        item.setCantidad(1);
        request.setItems(List.of(item));

        assertThatThrownBy(() -> pedidoService.crearPedido(request))
                .isInstanceOf(PedidoNoValidoException.class)
                .hasMessageContaining("no disponible");
    }

    @Test
    void crearPedido_conStockInsuficiente_debeLanzarExcepcion() {
        Producto producto = Producto.builder()
                .id(1L).nombre("Laptop").precio(BigDecimal.valueOf(10000))
                .stock(1).stockMinimo(3).activo(true)
                .build();

        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setClienteNombre("Juan");
        request.setClienteEmail("juan@test.com");
        PedidoRequestDTO.ItemRequest item = new PedidoRequestDTO.ItemRequest();
        item.setProductoId(1L);
        item.setCantidad(5);
        request.setItems(List.of(item));

        assertThatThrownBy(() -> pedidoService.crearPedido(request))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void confirmarPago_conEstadoSucceeded_debeActualizarEstadoYDeducirStock() throws Exception {
        Producto producto = Producto.builder()
                .id(1L).nombre("Laptop").precio(BigDecimal.valueOf(10000))
                .stock(10).stockMinimo(3).activo(true)
                .build();

        PedidoItem item = PedidoItem.builder()
                .id(1L).producto(producto).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subtotal(BigDecimal.valueOf(20000))
                .build();

        Pago pago = Pago.builder()
                .id(1L).paymentIntentId("pi_test_123")
                .estado(EstadoPago.PENDIENTE)
                .monto(BigDecimal.valueOf(20000))
                .build();

        Pedido pedido = Pedido.builder()
                .id(1L).clienteNombre("Juan").clienteEmail("juan@test.com")
                .total(BigDecimal.valueOf(20000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .fechaCreacion(LocalDateTime.now())
                .items(List.of(item))
                .pago(pago)
                .build();
        item.setPedido(pedido);
        pago.setPedido(pedido);

        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getStatus()).thenReturn("succeeded");
        when(mockPi.toJson()).thenReturn("{\"status\":\"succeeded\"}");

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(pedido));
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));
        when(stripeService.retrievePaymentIntent("pi_test_123")).thenReturn(mockPi);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoDTO resultado = pedidoService.confirmarPago(1L, TOKEN);

        assertThat(resultado.getEstado()).isEqualTo("PAGADO");
        assertThat(resultado.getPago().getEstado()).isEqualTo("COMPLETADO");
        assertThat(producto.getStock()).isEqualTo(8);
    }

    @Test
    void confirmarPago_conTokenInvalido_debeLanzarExcepcion() {
        Pedido pedido = Pedido.builder()
                .id(1L).clienteNombre("Juan").clienteEmail("juan@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.confirmarPago(1L, "wrong-token"))
                .isInstanceOf(PedidoNoValidoException.class)
                .hasMessageContaining("Token");
    }

    @Test
    void confirmarPago_conEstadoSucceededYPedidoYaPagado_debeSerIdempotente() throws Exception {
        Pedido pedido = Pedido.builder()
                .id(1L).clienteNombre("Juan").clienteEmail("juan@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PAGADO)
                .tokenConfirmacion(TOKEN)
                .build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(pedido));

        PedidoDTO resultado = pedidoService.confirmarPago(1L, TOKEN);

        assertThat(resultado.getEstado()).isEqualTo("PAGADO");
        verifyNoInteractions(stripeService);
    }

    @Test
    void confirmarPago_conEstadoCanceled_debeMarcarComoCancelado() throws Exception {
        Pago pago = Pago.builder()
                .id(1L).paymentIntentId("pi_test_456")
                .estado(EstadoPago.PENDIENTE)
                .monto(BigDecimal.valueOf(10000))
                .build();

        Pedido pedido = Pedido.builder()
                .id(2L).clienteNombre("Ana").clienteEmail("ana@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .fechaCreacion(LocalDateTime.now())
                .items(List.of())
                .pago(pago)
                .build();
        pago.setPedido(pedido);

        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getStatus()).thenReturn("canceled");

        when(pedidoRepository.findByIdWithItems(2L)).thenReturn(Optional.of(pedido));
        when(stripeService.retrievePaymentIntent("pi_test_456")).thenReturn(mockPi);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoDTO resultado = pedidoService.confirmarPago(2L, TOKEN);

        assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
        assertThat(resultado.getPago().getEstado()).isEqualTo("FALLIDO");
    }

    @Test
    void confirmarPagoWebhook_debeEncontrarPagoPorPaymentIntentIdYConfirmar() throws Exception {
        Producto producto = Producto.builder()
                .id(1L).nombre("Laptop").precio(BigDecimal.valueOf(10000))
                .stock(10).stockMinimo(3).activo(true)
                .build();

        PedidoItem item = PedidoItem.builder()
                .id(1L).producto(producto).cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subtotal(BigDecimal.valueOf(20000))
                .build();

        Pago pago = Pago.builder()
                .id(1L).paymentIntentId("pi_webhook_001")
                .estado(EstadoPago.PENDIENTE)
                .monto(BigDecimal.valueOf(20000))
                .build();

        Pedido pedido = Pedido.builder()
                .id(1L).clienteNombre("Juan").clienteEmail("juan@test.com")
                .total(BigDecimal.valueOf(20000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .fechaCreacion(LocalDateTime.now())
                .items(List.of(item))
                .pago(pago)
                .build();
        item.setPedido(pedido);
        pago.setPedido(pedido);

        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getStatus()).thenReturn("succeeded");

        when(pagoRepository.findByPaymentIntentIdWithPedido("pi_webhook_001")).thenReturn(Optional.of(pago));
        when(stripeService.retrievePaymentIntent("pi_webhook_001")).thenReturn(mockPi);
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoDTO resultado = pedidoService.confirmarPagoWebhook("pi_webhook_001");

        assertThat(resultado.getEstado()).isEqualTo("PAGADO");
    }

    @Test
    void cancelarPedido_debeCambiarEstadoYCancelarPaymentIntent() throws Exception {
        Pago pago = Pago.builder()
                .id(1L).paymentIntentId("pi_test_789")
                .estado(EstadoPago.PENDIENTE)
                .monto(BigDecimal.valueOf(10000))
                .build();

        Pedido pedido = Pedido.builder()
                .id(3L).clienteNombre("Luis").clienteEmail("luis@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .fechaCreacion(LocalDateTime.now())
                .pago(pago)
                .build();
        pago.setPedido(pedido);

        PaymentIntent mockPi = mock(PaymentIntent.class);
        when(mockPi.getStatus()).thenReturn("requires_payment_method");

        when(pedidoRepository.findByIdWithItems(3L)).thenReturn(Optional.of(pedido));
        when(stripeService.retrievePaymentIntent("pi_test_789")).thenReturn(mockPi);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoDTO resultado = pedidoService.cancelarPedido(3L, TOKEN);

        assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
        verify(mockPi).cancel();
    }

    @Test
    void cancelarPedido_conTokenInvalido_debeLanzarExcepcion() {
        Pedido pedido = Pedido.builder()
                .id(1L).clienteNombre("Luis").clienteEmail("luis@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cancelarPedido(1L, "wrong-token"))
                .isInstanceOf(PedidoNoValidoException.class)
                .hasMessageContaining("Token");
    }

    @Test
    void cancelarPedido_conTokenNull_debePermitirSinVerificacion() {
        Pedido pedido = Pedido.builder()
                .id(1L).clienteNombre("Luis").clienteEmail("luis@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PENDIENTE)
                .tokenConfirmacion(TOKEN)
                .build();

        when(pedidoRepository.findByIdWithItems(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoDTO resultado = pedidoService.cancelarPedido(1L, null);

        assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
    }

    @Test
    void cancelarPedido_conPedidoPagado_debeLanzarExcepcion() {
        Pedido pedido = Pedido.builder()
                .id(4L).clienteNombre("Luis").clienteEmail("luis@test.com")
                .total(BigDecimal.valueOf(10000))
                .estado(EstadoPedido.PAGADO)
                .tokenConfirmacion(TOKEN)
                .build();

        when(pedidoRepository.findByIdWithItems(4L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cancelarPedido(4L, TOKEN))
                .isInstanceOf(PedidoNoValidoException.class)
                .hasMessageContaining("pendientes");
    }

    @Test
    void obtenerPedido_conIdInexistente_debeLanzarResourceNotFoundException() {
        when(pedidoRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.obtenerPedido(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
