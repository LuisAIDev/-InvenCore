package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Categoría visible en el catálogo público")
public class CategoriaPublicaDTO {

    @Schema(description = "ID de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Electrónicos")
    private String nombre;
}
