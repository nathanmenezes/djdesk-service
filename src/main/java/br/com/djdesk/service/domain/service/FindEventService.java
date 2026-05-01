package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.in.FindEventUseCase;
import br.com.djdesk.service.domain.port.out.EventRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class FindEventService implements FindEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(FindEventService.class);
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private final EventRepositoryPort eventRepositoryPort;

    public FindEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findBySlug(UUID slug) {
        log.info("Finding event [slug={}, correlationId={}]", slug, MDC.get(CORRELATION_ID_MDC_KEY));
        return eventRepositoryPort.findBySlug(slug);
    }
}
