package br.com.djdesk.service.adapter.in.websocket.dto;

public record ChatStreamMessage(
        String type,
        String content,
        String sessionId
) {

    public static ChatStreamMessage chunk(String sessionId, String content) {
        return new ChatStreamMessage("CHUNK", content, sessionId);
    }

    public static ChatStreamMessage done(String sessionId) {
        return new ChatStreamMessage("DONE", null, sessionId);
    }

    public static ChatStreamMessage error(String sessionId, String message) {
        return new ChatStreamMessage("ERROR", message, sessionId);
    }

    public static ChatStreamMessage sessionStarted(String sessionId) {
        return new ChatStreamMessage("SESSION_STARTED", null, sessionId);
    }

    public static ChatStreamMessage briefingComplete(String sessionId) {
        return new ChatStreamMessage("BRIEFING_COMPLETE", null, sessionId);
    }
}
