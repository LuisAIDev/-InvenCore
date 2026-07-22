package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Oferta o descuento aplicable a productos")
public class OfertaDTO {

    @Schema(description = "ID de la oferta (autogenerado)", example = "1")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre de la oferta", example = "Black Friday 2026")
    private String nombre;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @Schema(description = "Porcentaje de descuento", example = "15.00")
    private BigDecimal porcentajeDescuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha de inicio de la oferta (ISO-8601)", example = "2026-07-01T00:00:00")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(description = "Fecha de fin de la oferta (ISO-8601)", example = "2026-07-31T23:59:59")
    private LocalDateTime fechaFin;

    @Schema(description = "Indica si la oferta está activa", example = "true")
    private Boolean activa;

    @Schema(description = "IDs de los productos incluidos en la oferta", example = "[1, 2, 3]")
    private List<Long> productoIds;
}
