package com.invencore.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean activo;
    private String imagenUrl;
    private Long categoriaId;
    private String categoriaNombre;
}
