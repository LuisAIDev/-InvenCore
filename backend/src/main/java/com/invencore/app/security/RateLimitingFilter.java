package com.invencore.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invencore.app.model.dto.ApiErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final Bandwidth LOGIN_BANDWIDTH = Bandwidth.simple(5, Duration.ofMinutes(1));
    private static final Bandwidth GENERAL_BANDWIDTH = Bandwidth.simple(100, Duration.ofMinutes(1));

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        if (path.equals("/api/auth/login")) {
            Bucket bucket = loginBuckets.computeIfAbsent(clientIp,
                    k -> Bucket.builder().addLimit(LOGIN_BANDWIDTH).build());
            if (!bucket.tryConsume(1)) {
                log.warn("Rate limit exceeded for /api/auth/login - IP: {}", clientIp);
                writeTooManyRequests(response, request);
                return;
            }
        } else {
            Bucket bucket = generalBuckets.computeIfAbsent(clientIp,
                    k -> Bucket.builder().addLimit(GENERAL_BANDWIDTH).build());
            if (!bucket.tryConsume(1)) {
                log.warn("Rate limit exceeded for general API - IP: {}, path: {}", clientIp, path);
                writeTooManyRequests(response, request);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletResponse servletResponse, HttpServletRequest request)
            throws IOException {
        servletResponse.setStatus(429);
        servletResponse.setContentType("application/json");

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(429)
                .error("Too Many Requests")
                .message("Has excedido el límite de solicitudes. Intenta de nuevo en un minuto.")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .traceId((String) request.getAttribute("traceId"))
                .build();

        objectMapper.writeValue(servletResponse.getWriter(), error);
    }

    void resetBuckets() {
        loginBuckets.clear();
        generalBuckets.clear();
    }
}
