package com.invencore.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "InvenCore API",
        description = "API REST del sistema de inventario empresarial InvenCore. "
                    + "Gestiona productos, categorías, movimientos de inventario, "
                    + "ofertas, pedidos y pagos con Stripe.",
        version = "1.0.0",
        contact = @Contact(
            name = "InvenCore Team",
            email = "soporte@invencore.com",
            url = "https://invencore.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Local"
        ),
        @Server(
            url = "https://invencore.onrender.com",
            description = "Producción (Render)"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Ingresa el token JWT obtenido en el endpoint /api/auth/login"
)
public class OpenApiConfig {
}
