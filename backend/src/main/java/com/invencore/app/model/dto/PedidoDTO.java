package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Pedido realizado por un cliente")
public class PedidoDTO {

    @Schema(description = "ID del pedido (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String clienteNombre;

    @Schema(description = "Correo electrónico del cliente", example = "juan@ejemplo.com")
    private String clienteEmail;

    @Schema(description = "Estado del pedido", example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "PAGADO", "CANCELADO"})
    private String estado;

    @Schema(description = "Monto total del pedido en MXN", example = "23999.98")
    private BigDecimal total;

    @Schema(description = "Fecha de creación (ISO-8601)", example = "2026-07-20T15:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Client secret de Stripe para completar el pago")
    private String clientSecret;

    @Schema(description = "Token de confirmación del pedido")
    private String tokenConfirmacion;

    @Schema(description = "Productos incluidos en el pedido")
    private List<PedidoItemDTO> items;

    @Schema(description = "Información del pago asociado")
    private PagoDTO pago;
}
