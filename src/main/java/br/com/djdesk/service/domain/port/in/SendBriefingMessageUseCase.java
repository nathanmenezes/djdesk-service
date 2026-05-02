package br.com.djdesk.service.domain.port.in;

public interface SendBriefingMessageUseCase {

    void send(String sessionId, String message);
}
