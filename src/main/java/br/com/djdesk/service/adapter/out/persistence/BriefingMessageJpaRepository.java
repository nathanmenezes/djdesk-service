package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.BriefingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BriefingMessageJpaRepository extends JpaRepository<BriefingMessage, Long> {

    @Query("SELECT m FROM BriefingMessage m WHERE m.session.slug = :sessionSlug ORDER BY m.createdAt ASC")
    List<BriefingMessage> findAllBySessionSlugOrderByCreatedAtAsc(@Param("sessionSlug") UUID sessionSlug);
}
