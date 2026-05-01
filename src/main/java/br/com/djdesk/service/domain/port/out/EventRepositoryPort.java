package br.com.djdesk.service.domain.port.out;

import br.com.djdesk.service.domain.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryPort {

    Event save(Event event);

    List<Event> findAllByDjSlug(UUID djSlug);

    Optional<Event> findBySlug(UUID slug);
}
