package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.adapter.in.web.dto.PublicEventResponse;

import java.util.UUID;

public interface GetPublicEventUseCase {

    PublicEventResponse getByPublicToken(UUID publicToken);
}
