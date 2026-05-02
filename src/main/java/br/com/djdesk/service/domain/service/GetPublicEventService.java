package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.web.dto.PublicEventResponse;
import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.in.GetPublicEventUseCase;
import br.com.djdesk.service.domain.port.out.EventRepositoryPort;
import br.com.djdesk.service.shared.enums.ErrorCode;
import br.com.djdesk.service.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetPublicEventService implements GetPublicEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetPublicEventService.class);

    private final EventRepositoryPort eventRepositoryPort;

    public GetPublicEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicEventResponse getByPublicToken(UUID publicToken) {
        log.info("Fetching public event [publicToken={}]", publicToken);
        Event event = eventRepositoryPort.findByPublicLink(publicToken)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND, "Event not found for token: " + publicToken));
        return new PublicEventResponse(
                event.getPublicLink(),
                event.getName(),
                event.getClientName(),
                event.getType(),
                event.getEventDate(),
                event.getDj().getArtisticName()
        );
    }
}
