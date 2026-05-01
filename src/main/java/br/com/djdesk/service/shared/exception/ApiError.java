package br.com.djdesk.service.shared.exception;

import java.time.Instant;

import br.com.djdesk.service.shared.enums.ErrorCode;

public record ApiError(
        String correlationId,
        Instant timestamp,
        int status,
        String errorCode,
        String messageKey,
        String message,
        String path
) {
    public static ApiError of(
            String correlationId,
            int status,
            ErrorCode errorCode,
            String message,
            String path
    ) {
        return new ApiError(
                correlationId,
                Instant.now(),
                status,
                errorCode.getCode(),
                errorCode.getMessageKey(),
                message,
                path
        );
    }

    public static ApiError generic(
            String correlationId,
            int status,
            String message,
            String path
    ) {
        return new ApiError(
                correlationId,
                Instant.now(),
                status,
                null,
                null,
                message,
                path
        );
    }
}
