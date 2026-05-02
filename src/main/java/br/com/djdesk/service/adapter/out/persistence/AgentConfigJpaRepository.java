package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.AgentConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentConfigJpaRepository extends JpaRepository<AgentConfig, Long> {

    Optional<AgentConfig> findByNameAndActiveTrue(String name);
}
