package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.BriefingResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BriefingResultJpaRepository extends JpaRepository<BriefingResult, Long> {
}
