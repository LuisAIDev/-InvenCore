package com.invencore.app.model.dto;

import com.invencore.app.model.entity.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoDTO {
    private Long id;
    @NotNull(message = "El tipo es obligatorio")
    private TipoMovimiento tipo;
    @NotNull
    @Min(1)
    private Integer cantidad;
    private String descripcion;
    private String motivo;
    private LocalDateTime fecha;
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    private String productoNombre;
    private Long usuarioId;
    private String usuarioNombre;
}
