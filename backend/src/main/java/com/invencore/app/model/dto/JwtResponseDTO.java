package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class JwtResponseDTO {

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBpbnZlbmNvcmUuY29tIiwiaWF0IjoxNzE...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tipo = "Bearer";

    @Schema(description = "Correo electrónico del usuario autenticado", example = "admin@invencore.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "ADMIN", allowableValues = {"ADMIN", "OPERADOR"})
    private String rol;
}
