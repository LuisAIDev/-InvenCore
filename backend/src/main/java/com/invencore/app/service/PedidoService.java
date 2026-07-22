package com.invencore.app.service;

import com.invencore.app.model.dto.PedidoDTO;
import com.invencore.app.model.dto.PedidoRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PedidoService {
    PedidoDTO crearPedido(PedidoRequestDTO request);
    PedidoDTO confirmarPago(Long pedidoId, String token);
    PedidoDTO confirmarPagoWebhook(String paymentIntentId);
    PedidoDTO cancelarPedido(Long pedidoId, String token);
    PedidoDTO obtenerPedido(Long pedidoId);
    List<PedidoDTO> listarTodos();
    Page<PedidoDTO> listarTodos(Pageable pageable);
}
