package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.BriefingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BriefingSessionJpaRepository extends JpaRepository<BriefingSession, Long> {

    Optional<BriefingSession> findBySlug(UUID slug);
}
