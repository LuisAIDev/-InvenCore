package com.invencore.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private String email;
    private String rol;
}
