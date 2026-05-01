package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.domain.enums.EventStatus;
import br.com.djdesk.service.shared.entity.AbstractAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event extends AbstractAuditEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "client_name", nullable = false, length = 150)
    private String clientName;

    @Column(name = "type", length = 100)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private EventStatus status;

    @Column(name = "public_link", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID publicLink;

    @Column(name = "event_date")
    private Instant eventDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_info")
    private Map<String, Object> additionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dj_id", nullable = false)
    private User dj;

    protected Event() {
        super();
    }

    public Event(
            String name,
            String clientName,
            String type,
            Instant eventDate,
            Map<String, Object> additionalInfo,
            User dj
    ) {
        super();
        this.name = name;
        this.clientName = clientName;
        this.type = type;
        this.eventDate = eventDate;
        this.additionalInfo = additionalInfo;
        this.dj = dj;
        this.status = EventStatus.CREATED;
        this.publicLink = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public String getClientName() {
        return clientName;
    }

    public String getType() {
        return type;
    }

    public EventStatus getStatus() {
        return status;
    }

    public UUID getPublicLink() {
        return publicLink;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public User getDj() {
        return dj;
    }
}
