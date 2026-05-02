package br.com.djdesk.service.adapter.in.web.dto;

import java.time.Instant;
import java.util.UUID;

public record PublicEventResponse(
        UUID publicLink,
        String name,
        String clientName,
        String type,
        Instant eventDate,
        String djArtisticName
) {
}
