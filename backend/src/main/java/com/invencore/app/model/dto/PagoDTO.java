package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Información del pago asociado a un pedido")
public class PagoDTO {

    @Schema(description = "ID del pago (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "ID del PaymentIntent de Stripe", example = "pi_3OzS9H2eZvKYlo2C0fGJLw8T")
    private String paymentIntentId;

    @Schema(description = "Estado del pago", example = "COMPLETADO",
            allowableValues = {"PENDIENTE", "COMPLETADO", "FALLIDO", "REEMBOLSADO"})
    private String estado;

    @Schema(description = "Monto pagado en MXN", example = "23999.98")
    private BigDecimal monto;

    @Schema(description = "Fecha de creación del pago (ISO-8601)", example = "2026-07-20T15:31:00")
    private LocalDateTime fechaCreacion;
}
