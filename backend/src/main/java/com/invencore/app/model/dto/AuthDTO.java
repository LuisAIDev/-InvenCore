package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciales de inicio de sesión")
public class AuthDTO {

    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Correo electrónico del usuario", example = "admin@invencore.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "miPassword123")
    private String password;
}
