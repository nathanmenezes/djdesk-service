package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.AgentConfig;
import br.com.djdesk.service.domain.port.out.AgentConfigRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AgentConfigPersistenceAdapter implements AgentConfigRepositoryPort {

    private final AgentConfigJpaRepository agentConfigJpaRepository;

    public AgentConfigPersistenceAdapter(AgentConfigJpaRepository agentConfigJpaRepository) {
        this.agentConfigJpaRepository = agentConfigJpaRepository;
    }

    @Override
    public Optional<AgentConfig> findByName(String name) {
        return agentConfigJpaRepository.findByNameAndActiveTrue(name);
    }
}
