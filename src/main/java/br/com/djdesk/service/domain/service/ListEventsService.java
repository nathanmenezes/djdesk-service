package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.in.ListEventsUseCase;
import br.com.djdesk.service.domain.port.out.EventRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListEventsService implements ListEventsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListEventsService.class);
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private final EventRepositoryPort eventRepositoryPort;

    public ListEventsService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> listByDj(UUID djSlug) {
        log.info("Listing events [djSlug={}, correlationId={}]", djSlug, MDC.get(CORRELATION_ID_MDC_KEY));
        return eventRepositoryPort.findAllByDjSlug(djSlug);
    }
}
