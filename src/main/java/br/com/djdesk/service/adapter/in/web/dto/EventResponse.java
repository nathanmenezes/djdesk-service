package br.com.djdesk.service.adapter.in.web.dto;

import br.com.djdesk.service.domain.enums.EventStatus;
import br.com.djdesk.service.domain.model.Event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EventResponse(
        UUID slug,
        String name,
        String clientName,
        String type,
        EventStatus status,
        UUID publicLink,
        Instant eventDate,
        Map<String, Object> additionalInfo,
        Instant createdAt
) {
    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getSlug(),
                event.getName(),
                event.getClientName(),
                event.getType(),
                event.getStatus(),
                event.getPublicLink(),
                event.getEventDate(),
                event.getAdditionalInfo(),
                event.getCreatedAt()
        );
    }
}
