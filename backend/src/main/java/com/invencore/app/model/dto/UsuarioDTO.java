package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Usuario del sistema (sin datos sensibles)")
public class UsuarioDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Correo electrónico", example = "admin@invencore.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String rol;

    @Schema(description = "Si la cuenta está activa", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha de creación", example = "2026-07-20T15:30:00")
    private LocalDateTime fechaCreacion;
}
