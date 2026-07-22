package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Solicitud para crear un nuevo pedido")
public class PedidoRequestDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String clienteNombre;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "Email inválido")
    @Schema(description = "Correo electrónico del cliente", example = "juan@ejemplo.com")
    private String clienteEmail;

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Schema(description = "Productos a incluir en el pedido")
    private List<ItemRequest> items;

    @Data
    @Schema(description = "Producto y cantidad solicitada")
    public static class ItemRequest {

        @Positive
        @Schema(description = "ID del producto", example = "1")
        private Long productoId;

        @Positive
        @Schema(description = "Cantidad deseada", example = "2")
        private Integer cantidad;
    }
}
