package br.com.djdesk.service.shared.exception;

import br.com.djdesk.service.shared.enums.ErrorCode;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
    }
}
