package com.invencore.app.controller;

import com.invencore.app.model.dto.AuthDTO;
import com.invencore.app.model.dto.JwtResponseDTO;
import com.invencore.app.model.dto.RegistroDTO;
import com.invencore.app.model.entity.RolUsuario;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.repository.UsuarioRepository;
import com.invencore.app.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registro e inicio de sesión")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = JwtResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody AuthDTO dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
            Usuario usuario = (Usuario) auth.getPrincipal();
            String token = jwtUtils.generateToken(usuario.getEmail());
            log.info("Login exitoso: email={}, rol={}", usuario.getEmail(), usuario.getRol());
            return ResponseEntity.ok(new JwtResponseDTO(
                    token, "Bearer", usuario.getEmail(), usuario.getRol().name()));
        } catch (BadCredentialsException e) {
            log.warn("Login fallido: credenciales inválidas para email={}", dto.getEmail());
            throw e;
        }
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario ADMIN u OPERADOR")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El email ya está registrado"));
        }
        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(dto.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Rol inválido. Use ADMIN u OPERADOR"));
        }
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(rol)
                .activo(true)
                .build();
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuario registrado correctamente"));
    }
}
