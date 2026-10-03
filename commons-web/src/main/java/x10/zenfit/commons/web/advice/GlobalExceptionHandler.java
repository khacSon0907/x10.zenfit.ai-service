package x10.zenfit.commons.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import x10.zenfit.common.dto.response.ErrorResponse;
import x10.zenfit.common.exceptions.BusinessException;
import x10.zenfit.common.exceptions.CommonError;
import x10.zenfit.common.exceptions.ErrorDescriptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* 1. BUSINESS */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        ErrorDescriptor error = ex.getError();
        log.warn("[BUSINESS_ERROR] code={}, message={}", error.code(), ex.getMessage());
        return build(error, ex.getMessage(), null, req);
    }

    /* 2. VALIDATION: @Valid DTO */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.computeIfAbsent(err.getField(), k -> new ArrayList<>())
                        .add(err.getDefaultMessage()));

        log.warn("[VALIDATION_ERROR] path={}, errors={}", req.getRequestURI(), fieldErrors);
        return build(CommonError.VALIDATION_ERROR, null, fieldErrors, req);
    }

    /* 3. VALIDATION: @RequestParam / @PathVariable */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v ->
                fieldErrors.computeIfAbsent(v.getPropertyPath().toString(), k -> new ArrayList<>())
                        .add(v.getMessage()));

        log.warn("[CONSTRAINT_VIOLATION] path={}, errors={}", req.getRequestURI(), fieldErrors);
        return build(CommonError.VALIDATION_ERROR, null, fieldErrors, req);
    }

    /* 4. ILLEGAL ARGUMENT */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("[ILLEGAL_ARGUMENT] path={}, message={}", req.getRequestURI(), ex.getMessage());
        return build(CommonError.INVALID_REQUEST, ex.getMessage(), null, req);
    }

    /* 5. SECURITY */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest req) {
        log.warn("[ACCESS_DENIED] path={}, message={}", req.getRequestURI(), ex.getMessage());
        return build(CommonError.FORBIDDEN, null, null, req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest req) {
        log.warn("[AUTHENTICATION_ERROR] path={}, message={}", req.getRequestURI(), ex.getMessage());
        return build(CommonError.UNAUTHORIZED, null, null, req);
    }

    /* 6. STATIC RESOURCE (favicon...) */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound() {
        return ResponseEntity.notFound().build();
    }

    /* 7. FALLBACK */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest req) {
        log.error("[SYSTEM_ERROR] path={}", req.getRequestURI(), ex);
        return build(CommonError.INTERNAL_SERVER_ERROR, null, null, req);
    }

    /* HELPER: message == null thì dùng defaultMessage của error */
    private ResponseEntity<ErrorResponse> build(
            ErrorDescriptor error, String message, Object detail, HttpServletRequest req) {
        return ResponseEntity
                .status(error.httpStatus())
                .body(ErrorResponse.of(
                        error.type(),
                        error.httpStatus(),
                        error.code(),
                        message != null ? message : error.defaultMessage(),
                        detail,
                        req.getRequestURI(),
                        MDC.get("traceId")));
    }
}