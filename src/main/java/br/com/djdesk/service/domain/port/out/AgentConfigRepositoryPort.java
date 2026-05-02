package br.com.djdesk.service.domain.port.out;

import br.com.djdesk.service.domain.model.AgentConfig;

import java.util.Optional;

public interface AgentConfigRepositoryPort {

    Optional<AgentConfig> findByName(String name);
}
