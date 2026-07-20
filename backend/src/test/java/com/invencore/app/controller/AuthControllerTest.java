package com.invencore.app.controller;

import com.invencore.app.model.dto.AuthDTO;
import com.invencore.app.model.dto.JwtResponseDTO;
import com.invencore.app.model.entity.RolUsuario;
import com.invencore.app.model.entity.Usuario;
import com.invencore.app.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_conCredencialesValidas_debeRetornarJwt() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("admin@invencore.com")
                .password("encoded-pass")
                .rol(RolUsuario.ADMIN)
                .nombre("Admin")
                .activo(true)
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtUtils.generateToken("admin@invencore.com")).thenReturn("fake-jwt-token");

        AuthDTO dto = new AuthDTO();
        dto.setEmail("admin@invencore.com");
        dto.setPassword("correct-password");

        ResponseEntity<JwtResponseDTO> response = authController.login(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getBody().getEmail()).isEqualTo("admin@invencore.com");
        assertThat(response.getBody().getRol()).isEqualTo("ADMIN");
        assertThat(response.getBody().getTipo()).isEqualTo("Bearer");
    }

    @Test
    void login_conCredencialesInvalidas_debeLanzarBadCredentialsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthDTO dto = new AuthDTO();
        dto.setEmail("admin@invencore.com");
        dto.setPassword("wrong-password");

        assertThrows(BadCredentialsException.class, () -> authController.login(dto));
        verify(jwtUtils, never()).generateToken(anyString());
    }
}
