package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.domain.enums.BriefingStatus;
import br.com.djdesk.service.shared.entity.AbstractAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "briefing_sessions")
public class BriefingSession extends AbstractAuditEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BriefingStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected BriefingSession() {
        super();
    }

    public BriefingSession(Event event) {
        super();
        this.event = event;
        this.status = BriefingStatus.ACTIVE;
        this.startedAt = Instant.now();
    }

    public void complete() {
        this.status = BriefingStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void abandon() {
        this.status = BriefingStatus.ABANDONED;
        this.completedAt = Instant.now();
    }

    public Event getEvent() {
        return event;
    }

    public BriefingStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
