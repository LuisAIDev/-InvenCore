package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Producto individual dentro de un pedido")
public class PedidoItemDTO {

    @Schema(description = "ID del detalle (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "ID del producto", example = "1")
    private Long productoId;

    @Schema(description = "Nombre del producto al momento de la compra", example = "Laptop Gamer X200")
    private String productoNombre;

    @Schema(description = "URL de la imagen del producto", example = "https://ejemplo.com/img/laptop.jpg")
    private String productoImagenUrl;

    @Schema(description = "Cantidad comprada", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario congelado al momento de la compra", example = "11999.99")
    private BigDecimal precioUnitario;

    @Schema(description = "Subtotal del producto (cantidad * precioUnitario)", example = "23999.98")
    private BigDecimal subtotal;

    @Schema(description = "Porcentaje de descuento aplicado", example = "25.00")
    private BigDecimal porcentajeDescuento;
}
