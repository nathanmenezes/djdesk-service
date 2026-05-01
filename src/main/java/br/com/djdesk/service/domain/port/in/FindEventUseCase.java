package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.domain.model.Event;

import java.util.Optional;
import java.util.UUID;

public interface FindEventUseCase {

    Optional<Event> findBySlug(UUID slug);
}
