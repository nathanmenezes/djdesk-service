package br.com.djdesk.service.domain.port.out;

import br.com.djdesk.service.domain.model.BriefingMessage;
import br.com.djdesk.service.domain.model.BriefingResult;
import br.com.djdesk.service.domain.model.BriefingSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BriefingRepositoryPort {

    BriefingSession saveSession(BriefingSession session);

    Optional<BriefingSession> findSessionBySlug(UUID slug);

    BriefingMessage saveMessage(BriefingMessage message);

    List<BriefingMessage> findMessagesBySessionSlug(UUID sessionSlug);

    BriefingResult saveResult(BriefingResult result);
}
