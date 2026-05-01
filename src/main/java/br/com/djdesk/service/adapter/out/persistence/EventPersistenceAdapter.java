package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.out.EventRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventPersistenceAdapter implements EventRepositoryPort {

    private final EventJpaRepository eventJpaRepository;

    public EventPersistenceAdapter(EventJpaRepository eventJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
    }

    @Override
    public Event save(Event event) {
        return eventJpaRepository.save(event);
    }

    @Override
    public List<Event> findAllByDjSlug(UUID djSlug) {
        return eventJpaRepository.findAllByDjSlug(djSlug);
    }

    @Override
    public Optional<Event> findBySlug(UUID slug) {
        return eventJpaRepository.findBySlug(slug);
    }
}
