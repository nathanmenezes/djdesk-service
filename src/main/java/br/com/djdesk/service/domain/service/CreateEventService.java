package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.web.dto.CreateEventRequest;
import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.model.User;
import br.com.djdesk.service.domain.port.in.CreateEventUseCase;
import br.com.djdesk.service.domain.port.out.EventRepositoryPort;
import br.com.djdesk.service.domain.port.out.UserRepositoryPort;
import br.com.djdesk.service.shared.enums.ErrorCode;
import br.com.djdesk.service.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateEventService implements CreateEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateEventService.class);
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private final EventRepositoryPort eventRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public CreateEventService(
            EventRepositoryPort eventRepositoryPort,
            UserRepositoryPort userRepositoryPort
    ) {
        this.eventRepositoryPort = eventRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    public Event create(CreateEventRequest request, UUID djSlug) {
        log.info("Creating event [djSlug={}, correlationId={}]", djSlug, MDC.get(CORRELATION_ID_MDC_KEY));

        User dj = userRepositoryPort.findBySlug(djSlug)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "DJ not found"));

        Event event = new Event(
                request.name(),
                request.clientName(),
                request.type(),
                request.eventDate(),
                request.additionalInfo(),
                dj
        );

        Event saved = eventRepositoryPort.save(event);
        log.info("Event created [slug={}, correlationId={}]", saved.getSlug(), MDC.get(CORRELATION_ID_MDC_KEY));

        return saved;
    }
}
