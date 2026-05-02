package br.com.djdesk.service.adapter.in.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BriefingAccumulatedData {

    private String vibe;
    private final List<String> preferredStyles = new ArrayList<>();
    private final List<String> forbiddenStyles = new ArrayList<>();
    private final List<Map<String, Object>> requiredSongs = new ArrayList<>();
    private final List<Map<String, Object>> forbiddenSongs = new ArrayList<>();
    private final List<Map<String, Object>> moments = new ArrayList<>();

    public String getVibe() {
        return vibe;
    }

    public void setVibe(String vibe) {
        this.vibe = vibe;
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
}
