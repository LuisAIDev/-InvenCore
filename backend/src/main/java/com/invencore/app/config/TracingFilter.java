package com.invencore.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TracingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TracingFilter.class);
    static final String TRACE_ID_HEADER = "X-Trace-Id";
    static final String TRACE_ID_ATTR = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        MDC.put(TRACE_ID_ATTR, traceId);
        request.setAttribute(TRACE_ID_ATTR, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String path = getRequestPath(request);

        String user = resolveUser(request);
        log.info("→ {} {} [{}]{}", method, path, traceId, user != null ? " user=" + user : "");

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            int status = response.getStatus();
            String level = status >= 500 ? "ERROR" : status >= 400 ? "WARN" : "INFO";
            log.info("← {} {} {} {}ms [{}] {}", level, method, path, elapsed, status, traceId);
            MDC.remove(TRACE_ID_ATTR);
        }
    }

    private static String getRequestPath(HttpServletRequest request) {
        String qs = request.getQueryString();
        return qs != null ? request.getRequestURI() + "?" + qs : request.getRequestURI();
    }

    private static String resolveUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return principal.getName();
        }
        return null;
    }
}
