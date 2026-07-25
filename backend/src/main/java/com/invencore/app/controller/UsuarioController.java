package com.invencore.app.controller;

import com.invencore.app.model.dto.UsuarioDTO;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios paginados (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios paginada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<Page<UsuarioDTO>> listarTodos(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        Page<UsuarioDTO> dtoPage = usuarioRepository.findAll(pageable)
                .map(this::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar/desactivar usuario", description = "Alterna el estado activo de un usuario (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "No se puede desactivar a sí mismo"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN")
    })
    public ResponseEntity<?> toggleEstado(@PathVariable Long id) {
        Usuario admin = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (admin.getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "No puedes desactivar tu propia cuenta de administrador"));
        }
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(!Boolean.TRUE.equals(usuario.getActivo()));
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok((Object) toDTO(usuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol().name());
        dto.setActivo(u.getActivo());
        dto.setFechaCreacion(u.getFechaCreacion());
        return dto;
    }
}
