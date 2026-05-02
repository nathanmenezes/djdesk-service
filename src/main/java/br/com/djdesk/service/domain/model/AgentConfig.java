package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.shared.entity.AbstractAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "agent_configs")
public class AgentConfig extends AbstractAuditEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "temperature", nullable = false, precision = 3, scale = 2)
    private BigDecimal temperature;

    @Column(name = "max_tokens", nullable = false)
    private int maxTokens;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "enabled_tools")
    private List<String> enabledTools;

    protected AgentConfig() {
        super();
    }

    public String getName() {
        return name;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public List<String> getEnabledTools() {
        return enabledTools;
    }
}
