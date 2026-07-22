package com.invencore.app.controller;

import com.invencore.app.model.dto.PedidoDTO;
import com.invencore.app.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pedidos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Orders", description = "Gestión de pedidos (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class PedidoAdminController {

    private final PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna todos los pedidos (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/pagina")
    @Operation(summary = "Listar pedidos paginados", description = "Retorna pedidos paginados (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de pedidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<Page<PedidoDTO>> listarTodosPaginado(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Retorna un pedido por su ID (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> obtener(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido pendiente (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
        @ApiResponse(responseCode = "400", description = "Pedido no pendiente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> cancelar(
            @Parameter(description = "ID del pedido") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, null));
    }
}
