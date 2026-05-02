package br.com.djdesk.service.domain.port.in;

import java.util.UUID;

public interface StartBriefingUseCase {

    String start(UUID publicToken);
}
