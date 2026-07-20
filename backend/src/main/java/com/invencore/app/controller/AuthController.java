package com.invencore.app.controller;

import com.invencore.app.model.dto.AuthDTO;
import com.invencore.app.model.dto.JwtResponseDTO;
import com.invencore.app.model.dto.RegistroDTO;
import com.invencore.app.model.entity.RolUsuario;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.repository.UsuarioRepository;
import com.invencore.app.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody AuthDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        Usuario usuario = (Usuario) auth.getPrincipal();
        String token = jwtUtils.generateToken(usuario.getEmail());
        return ResponseEntity.ok(new JwtResponseDTO(
                token, "Bearer", usuario.getEmail(), usuario.getRol().name()));
    }

    @PostMapping("/registro")
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
