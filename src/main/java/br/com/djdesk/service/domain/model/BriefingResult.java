package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.shared.entity.AbstractAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "briefing_results")
public class BriefingResult extends AbstractAuditEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "guest_profile", columnDefinition = "TEXT")
    private String guestProfile;

    @Column(name = "vibe", columnDefinition = "TEXT")
    private String vibe;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_styles")
    private List<String> preferredStyles;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "forbidden_styles")
    private List<String> forbiddenStyles;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_songs")
    private List<Map<String, Object>> requiredSongs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "forbidden_songs")
    private List<Map<String, Object>> forbiddenSongs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "moments")
    private List<Map<String, Object>> moments;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    protected BriefingResult() {
        super();
    }

    public BriefingResult(
            Event event,
            String summary,
            String guestProfile,
            String vibe,
            List<String> preferredStyles,
            List<String> forbiddenStyles,
            List<Map<String, Object>> requiredSongs,
            List<Map<String, Object>> forbiddenSongs,
            List<Map<String, Object>> moments
    ) {
        super();
        this.event = event;
        this.summary = summary;
        this.guestProfile = guestProfile;
        this.vibe = vibe;
        this.preferredStyles = preferredStyles;
        this.forbiddenStyles = forbiddenStyles;
        this.requiredSongs = requiredSongs;
        this.forbiddenSongs = forbiddenSongs;
        this.moments = moments;
        this.generatedAt = Instant.now();
    }

    public Event getEvent() {
        return event;
    }

    public String getSummary() {
        return summary;
    }

    public String getGuestProfile() {
        return guestProfile;
    }

    public String getVibe() {
        return vibe;
    }

    public List<String> getPreferredStyles() {
        return preferredStyles;
    }

    public List<String> getForbiddenStyles() {
        return forbiddenStyles;
    }

    public List<Map<String, Object>> getRequiredSongs() {
        return requiredSongs;
    }

    public List<Map<String, Object>> getForbiddenSongs() {
        return forbiddenSongs;
    }

    public List<Map<String, Object>> getMoments() {
        return moments;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
