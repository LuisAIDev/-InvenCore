package com.invencore.app.model.dto;

import com.invencore.app.model.entity.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Movimiento de inventario (entrada o salida)")
public class MovimientoDTO {

    @Schema(description = "ID del movimiento (autogenerado)", example = "1")
    private Long id;

    @NotNull(message = "El tipo es obligatorio")
    @Schema(description = "Tipo de movimiento", example = "ENTRADA", allowableValues = {"ENTRADA", "SALIDA"})
    private TipoMovimiento tipo;

    @NotNull
    @Min(1)
    @Schema(description = "Cantidad de unidades", example = "10")
    private Integer cantidad;

    @Schema(description = "Descripción del movimiento", example = "Compra a proveedor")
    private String descripcion;

    @Schema(description = "Motivo del movimiento", example = "Reposición de stock")
    private String motivo;

    @Schema(description = "Fecha del movimiento (ISO-8601)", example = "2026-07-20T10:30:00")
    private LocalDateTime fecha;

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto asociado", example = "1")
    private Long productoId;

    @Schema(description = "Nombre del producto (solo lectura)", example = "Laptop Gamer X200")
    private String productoNombre;

    @Schema(description = "ID del usuario que registró el movimiento", example = "1")
    private Long usuarioId;

    @Schema(description = "Nombre del usuario (solo lectura)", example = "Admin")
    private String usuarioNombre;
}
