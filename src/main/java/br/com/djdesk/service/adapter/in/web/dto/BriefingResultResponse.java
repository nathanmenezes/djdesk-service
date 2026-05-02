package br.com.djdesk.service.adapter.in.web.dto;

import br.com.djdesk.service.domain.model.BriefingResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BriefingResultResponse(
        UUID eventSlug,
        String summary,
        String guestProfile,
        String vibe,
        List<String> preferredStyles,
        List<String> forbiddenStyles,
        List<Map<String, Object>> requiredSongs,
        List<Map<String, Object>> forbiddenSongs,
        List<Map<String, Object>> moments,
        Instant generatedAt
) {
    public static BriefingResultResponse from(BriefingResult result) {
        return new BriefingResultResponse(
                result.getEvent().getSlug(),
                result.getSummary(),
                result.getGuestProfile(),
                result.getVibe(),
                result.getPreferredStyles(),
                result.getForbiddenStyles(),
                result.getRequiredSongs(),
                result.getForbiddenSongs(),
                result.getMoments(),
                result.getGeneratedAt()
        );
    }
}
