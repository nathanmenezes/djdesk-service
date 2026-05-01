package br.com.djdesk.service.shared.enums;

public enum ErrorCode {

    // User errors — ERR-USR-xxx
    USER_EMAIL_ALREADY_REGISTERED("ERR-USR-001", "error.user.email.already.registered"),

    // Auth errors — ERR-AUTH-xxx
    INVALID_CREDENTIALS("ERR-AUTH-001", "error.auth.invalid.credentials"),
    REFRESH_TOKEN_INVALID("ERR-AUTH-002", "error.auth.refresh.token.invalid"),
    REFRESH_TOKEN_EXPIRED("ERR-AUTH-003", "error.auth.refresh.token.expired");

    private final String code;
    private final String messageKey;

    ErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
