package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Producto del inventario")
public class ProductoDTO {

    @Schema(description = "ID del producto (autogenerado)", example = "1")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del producto", example = "Laptop Gamer X200")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Laptop con RTX 4060, 16GB RAM, 512GB SSD")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Schema(description = "Precio unitario en MXN", example = "15999.99")
    private BigDecimal precio;

    @Schema(description = "Stock actual", example = "25")
    private Integer stock;

    @Schema(description = "Stock mínimo antes de alerta", example = "5")
    private Integer stockMinimo;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean activo;

    @Schema(description = "URL de la imagen del producto", example = "https://ejemplo.com/img/laptop.jpg")
    private String imagenUrl;

    @Schema(description = "ID de la categoría", example = "1")
    private Long categoriaId;

    @Schema(description = "Nombre de la categoría (solo lectura)", example = "Electrónicos")
    private String categoriaNombre;
}
