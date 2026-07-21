package com.invencore.app.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoPublicoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Long categoriaId;
    private String categoriaNombre;
    private boolean disponible;

    private BigDecimal precioOriginal;
    private BigDecimal precioConDescuento;
    private BigDecimal porcentajeDescuento;
    private String ofertaNombre;
}
