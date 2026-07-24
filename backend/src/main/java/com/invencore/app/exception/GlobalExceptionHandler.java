package com.invencore.app.exception;

import com.invencore.app.model.dto.ApiErrorResponse;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String message = "Error de validación: " + String.join("; ", details);
        log.warn("{} {} — Validación: {}", request.getMethod(), request.getRequestURI(), message);
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        log.warn("{} {} — Violación de restricción: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Error de validación: " + ex.getMessage(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleJpaEntityNotFound(EntityNotFoundException ex,
                                                                     HttpServletRequest request) {
        log.warn("{} {} — No encontrado: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                    HttpServletRequest request) {
        log.warn("{} {} — No encontrado: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ApiErrorResponse> handleStockInsuficiente(StockInsuficienteException ex,
                                                                     HttpServletRequest request) {
        log.warn("{} {} — Stock insuficiente: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(PedidoNoValidoException.class)
    public ResponseEntity<ApiErrorResponse> handlePedidoNoValido(PedidoNoValidoException ex,
                                                                  HttpServletRequest request) {
        log.warn("{} {} — Pedido inválido: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PagoException.class)
    public ResponseEntity<ApiErrorResponse> handlePagoError(PagoException ex,
                                                             HttpServletRequest request) {
        log.error("{} {} — Error de pago: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex,
                                                            HttpServletRequest request) {
        log.warn("{} {} — Regla de negocio: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                   HttpServletRequest request) {
        log.warn("{} {} — Argumento inválido: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex,
                                                                HttpServletRequest request) {
        log.warn("{} {} — Estado inválido: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponse> handleSecurity(SecurityException ex,
                                                            HttpServletRequest request) {
        log.warn("{} {} — Seguridad: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                                HttpServletRequest request) {
        log.warn("{} {} — Acceso denegado: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex,
                                                                   HttpServletRequest request) {
        log.warn("{} {} — Autenticación fallida: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", request);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(OptimisticLockException ex,
                                                                   HttpServletRequest request) {
        log.warn("{} {} — Conflicto de concurrencia: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.CONFLICT,
                "El recurso fue modificado por otro usuario. Intenta de nuevo.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                 HttpServletRequest request) {
        log.warn("{} {} — Violación de integridad: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.CONFLICT, "Conflicto con datos existentes. Verifica que el registro no esté duplicado.", request);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiErrorResponse> handleStripe(StripeException ex,
                                                           HttpServletRequest request) {
        log.error("{} {} — Stripe error: code={}, msg={}, stripeError={}", request.getMethod(), request.getRequestURI(),
                ex.getCode(), ex.getMessage(),
                ex.getStripeError() != null ? ex.getStripeError().toJson() : "N/A", ex);
        return build(HttpStatus.BAD_GATEWAY, "Error en el procesador de pagos", request);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiErrorResponse> handleSQL(SQLException ex,
                                                       HttpServletRequest request) {
        log.error("{} {} — Error de base de datos: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno de base de datos", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex,
                                                           HttpServletRequest request) {
        log.error("{} {} — No manejado: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message,
                                                    HttpServletRequest request) {
        String traceId = (String) request.getAttribute("traceId");
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .traceId(traceId)
                .build();
        return new ResponseEntity<>(error, status);
    }
}
