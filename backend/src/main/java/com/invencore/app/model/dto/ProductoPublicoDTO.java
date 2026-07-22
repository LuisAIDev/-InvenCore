package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Producto visible en el catálogo público (con descuentos aplicados)")
public class ProductoPublicoDTO {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Gamer X200")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Laptop con RTX 4060, 16GB RAM, 512GB SSD")
    private String descripcion;

    @Schema(description = "Precio base del producto", example = "15999.99")
    private BigDecimal precio;

    @Schema(description = "URL de la imagen", example = "https://ejemplo.com/img/laptop.jpg")
    private String imagenUrl;

    @Schema(description = "ID de la categoría", example = "1")
    private Long categoriaId;

    @Schema(description = "Nombre de la categoría", example = "Electrónicos")
    private String categoriaNombre;

    @Schema(description = "Indica si el producto tiene stock disponible", example = "true")
    private boolean disponible;

    @Schema(description = "Precio original antes del descuento", example = "19999.99")
    private BigDecimal precioOriginal;

    @Schema(description = "Precio con descuento aplicado", example = "15999.99")
    private BigDecimal precioConDescuento;

    @Schema(description = "Porcentaje de descuento aplicado", example = "20.00")
    private BigDecimal porcentajeDescuento;

    @Schema(description = "Nombre de la oferta activa", example = "Verano 2026")
    private String ofertaNombre;
}
