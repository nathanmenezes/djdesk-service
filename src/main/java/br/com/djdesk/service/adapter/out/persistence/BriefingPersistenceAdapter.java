package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.BriefingMessage;
import br.com.djdesk.service.domain.model.BriefingResult;
import br.com.djdesk.service.domain.model.BriefingSession;
import br.com.djdesk.service.domain.port.out.BriefingRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BriefingPersistenceAdapter implements BriefingRepositoryPort {

    private final BriefingSessionJpaRepository sessionRepository;
    private final BriefingMessageJpaRepository messageRepository;
    private final BriefingResultJpaRepository resultRepository;

    public BriefingPersistenceAdapter(
            BriefingSessionJpaRepository sessionRepository,
            BriefingMessageJpaRepository messageRepository,
            BriefingResultJpaRepository resultRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public BriefingSession saveSession(BriefingSession session) {
        return sessionRepository.save(session);
    }

    @Override
    public Optional<BriefingSession> findSessionBySlug(UUID slug) {
        return sessionRepository.findBySlug(slug);
    }

    @Override
    public BriefingMessage saveMessage(BriefingMessage message) {
        return messageRepository.save(message);
    }

    @Override
    public List<BriefingMessage> findMessagesBySessionSlug(UUID sessionSlug) {
        return messageRepository.findAllBySessionSlugOrderByCreatedAtAsc(sessionSlug);
    }

    @Override
    public BriefingResult saveResult(BriefingResult result) {
        return resultRepository.save(result);
    }

    @Override
    public Optional<BriefingResult> findResultByEventSlug(UUID eventSlug) {
        return resultRepository.findByEventSlug(eventSlug);
    }
}
