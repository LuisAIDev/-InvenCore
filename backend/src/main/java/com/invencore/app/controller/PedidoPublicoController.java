package com.invencore.app.controller;

import com.invencore.app.model.dto.PedidoDTO;
import com.invencore.app.model.dto.PedidoRequestDTO;
import com.invencore.app.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publico/pedidos")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Pedidos del catálogo público (sin autenticación)")
public class PedidoPublicoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Crear pedido",
               description = "Crea un nuevo pedido con los productos del carrito. "
                           + "Retorna el client_secret para procesar el pago con Stripe.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente")
    })
    public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody PedidoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido", description = "Retorna los detalles de un pedido por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> obtener(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(id));
    }

    @PostMapping("/{id}/confirmar-pago")
    @Operation(summary = "Confirmar pago",
               description = "Confirma el pago de un pedido después de que Stripe haya procesado "
                           + "la tarjeta. Requiere el token de confirmación obtenido al crear el pedido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago confirmado"),
        @ApiResponse(responseCode = "400", description = "Token inválido o pedido no pendiente"),
        @ApiResponse(responseCode = "403", description = "Token de confirmación inválido"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> confirmarPago(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Parameter(description = "Token de confirmación (UUID)") @RequestParam String token) {
        return ResponseEntity.ok(pedidoService.confirmarPago(id, token));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido",
               description = "Cancela un pedido pendiente. Requiere el token de confirmación.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
        @ApiResponse(responseCode = "400", description = "Pedido no pendiente"),
        @ApiResponse(responseCode = "403", description = "Token de confirmación inválido"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> cancelar(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Parameter(description = "Token de confirmación (UUID)") @RequestParam String token) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, token));
    }
}
