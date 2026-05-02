package br.com.djdesk.service.shared.enums;

public enum ErrorCode {

    // User errors — ERR-USR-xxx
    USER_EMAIL_ALREADY_REGISTERED("ERR-USR-001", "error.user.email.already.registered"),
    USER_NOT_FOUND("ERR-USR-002", "error.user.not.found"),

    // Auth errors — ERR-AUTH-xxx
    INVALID_CREDENTIALS("ERR-AUTH-001", "error.auth.invalid.credentials"),
    REFRESH_TOKEN_INVALID("ERR-AUTH-002", "error.auth.refresh.token.invalid"),
    REFRESH_TOKEN_EXPIRED("ERR-AUTH-003", "error.auth.refresh.token.expired"),

    // Event errors — ERR-EVT-xxx
    EVENT_NOT_FOUND("ERR-EVT-001", "error.event.not.found"),

    // Briefing errors — ERR-BRF-xxx
    BRIEFING_SESSION_NOT_FOUND("ERR-BRF-001", "error.briefing.session.not.found"),
    AGENT_CONFIG_NOT_FOUND("ERR-BRF-002", "error.briefing.agent.config.not.found");

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
