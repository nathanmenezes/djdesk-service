package br.com.djdesk.service.shared.exception;

import br.com.djdesk.service.shared.enums.ErrorCode;

public class RefreshTokenInvalidException extends DomainException {

    public RefreshTokenInvalidException() {
        super(ErrorCode.REFRESH_TOKEN_INVALID, "Refresh token is invalid or revoked");
    }

    public RefreshTokenInvalidException(ErrorCode errorCode) {
        super(errorCode, "Refresh token is expired");
    }
}
