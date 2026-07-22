package com.invencore.app.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos para registrar un nuevo usuario")
public class RegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Schema(description = "Correo electrónico del usuario", example = "operador@invencore.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "miPasswordSegura456")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Schema(description = "Rol del usuario", example = "OPERADOR", allowableValues = {"ADMIN", "OPERADOR"})
    private String rol;
}
