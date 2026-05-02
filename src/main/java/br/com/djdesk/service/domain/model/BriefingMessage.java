package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.domain.enums.MessageRole;
import br.com.djdesk.service.shared.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "briefing_messages")
public class BriefingMessage extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private BriefingSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MessageRole role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    protected BriefingMessage() {
        super();
    }

    public BriefingMessage(BriefingSession session, MessageRole role, String content) {
        super();
        this.session = session;
        this.role = role;
        this.content = content;
    }

    public BriefingSession getSession() {
        return session;
    }

    public MessageRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
