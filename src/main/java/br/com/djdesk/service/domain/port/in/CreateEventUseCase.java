package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.adapter.in.web.dto.CreateEventRequest;
import br.com.djdesk.service.domain.model.Event;

import java.util.UUID;

public interface CreateEventUseCase {

    Event create(CreateEventRequest request, UUID djSlug);
}
