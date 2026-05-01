package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.domain.model.Event;

import java.util.List;
import java.util.UUID;

public interface ListEventsUseCase {

    List<Event> listByDj(UUID djSlug);
}
