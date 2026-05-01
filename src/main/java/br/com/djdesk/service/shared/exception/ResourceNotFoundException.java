package br.com.djdesk.service.shared.exception;

import br.com.djdesk.service.shared.enums.ErrorCode;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
