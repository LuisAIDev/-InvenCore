package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Categoría de productos")
public class CategoriaDTO {

    @Schema(description = "ID de la categoría (autogenerado)", example = "1")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre de la categoría", example = "Electrónicos")
    private String nombre;

    @Schema(description = "Descripción de la categoría", example = "Productos electrónicos y gadgets")
    private String descripcion;

    @Schema(description = "Indica si la categoría está activa", example = "true")
    private Boolean activo;
}
