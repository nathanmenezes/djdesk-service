package br.com.djdesk.service.shared.exception;

import br.com.djdesk.service.shared.enums.ErrorCode;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException() {
        super(ErrorCode.USER_EMAIL_ALREADY_REGISTERED, "E-mail is already registered");
    }
}
