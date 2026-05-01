package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.dj.slug = :djSlug ORDER BY e.createdAt DESC")
    List<Event> findAllByDjSlug(@Param("djSlug") UUID djSlug);

    Optional<Event> findBySlug(UUID slug);
}
