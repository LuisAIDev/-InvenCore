package com.invencore.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    private static final String TEST_IP = "99.99.99.99";
    private static final String LOGIN_BODY = """
            {
                "email": "admin@invencore.com",
                "password": "wrong-password"
            }
            """;

    @BeforeEach
    void setUp() {
        rateLimitingFilter.resetBuckets();
    }

    @Test
    void login_despuesDe5IntentosFallidos_debeRetornar429() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(LOGIN_BODY)
                            .with(request -> {
                                request.setRemoteAddr(TEST_IP);
                                return request;
                            }))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY)
                        .with(request -> {
                            request.setRemoteAddr(TEST_IP);
                            return request;
                        }))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void login_conIpDiferente_noDebeSerAfectado() throws Exception {
        String body = """
                {
                    "email": "admin@invencore.com",
                    "password": "wrong-password"
                }
                """;

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(request -> {
                                request.setRemoteAddr(TEST_IP);
                                return request;
                            }))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(request -> {
                            request.setRemoteAddr("88.88.88.88");
                            return request;
                        }))
                .andExpect(status().isUnauthorized());
    }
}
