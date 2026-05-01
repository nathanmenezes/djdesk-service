package br.com.djdesk.service.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found [code={}, correlationId={}]",
                ex.getErrorCode().getCode(), MDC.get(CORRELATION_ID_MDC_KEY));

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(
                        MDC.get(CORRELATION_ID_MDC_KEY),
                        HttpStatus.NOT_FOUND.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyRegistered(
            EmailAlreadyRegisteredException ex,
            HttpServletRequest request
    ) {
        log.warn("Domain exception [code={}, correlationId={}]",
                ex.getErrorCode().getCode(), MDC.get(CORRELATION_ID_MDC_KEY));

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(
                        MDC.get(CORRELATION_ID_MDC_KEY),
                        HttpStatus.CONFLICT.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {
        log.warn("Authentication failure [code={}, correlationId={}]",
                ex.getErrorCode().getCode(), MDC.get(CORRELATION_ID_MDC_KEY));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(
                        MDC.get(CORRELATION_ID_MDC_KEY),
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ApiError> handleRefreshTokenInvalid(
            RefreshTokenInvalidException ex,
            HttpServletRequest request
    ) {
        log.warn("Refresh token failure [code={}, correlationId={}]",
                ex.getErrorCode().getCode(), MDC.get(CORRELATION_ID_MDC_KEY));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(
                        MDC.get(CORRELATION_ID_MDC_KEY),
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldValidationError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldValidationError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        log.warn("Validation failure [fields={}, correlationId={}]",
                fieldErrors.stream().map(FieldValidationError::field).collect(Collectors.joining(", ")),
                MDC.get(CORRELATION_ID_MDC_KEY));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ValidationApiError(
                        MDC.get(CORRELATION_ID_MDC_KEY),
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "Validation failed",
                        request.getRequestURI(),
                        fieldErrors
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.generic(
                        MDC.get(CORRELATION_ID_MDC_KEY),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred",
                        request.getRequestURI()
                ));
    }

    public record FieldValidationError(String field, String message) {}

    public record ValidationApiError(
            String correlationId,
            int status,
            String message,
            String path,
            List<FieldValidationError> errors
    ) {}
}
