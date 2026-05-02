package br.com.djdesk.service.adapter.in.ai;

import br.com.djdesk.service.adapter.in.websocket.dto.ChatStreamMessage;
import br.com.djdesk.service.domain.enums.BriefingStatus;
import br.com.djdesk.service.domain.enums.MessageRole;
import br.com.djdesk.service.domain.model.BriefingMessage;
import br.com.djdesk.service.domain.model.BriefingResult;
import br.com.djdesk.service.domain.model.BriefingSession;
import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.out.BriefingRepositoryPort;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;

public class BriefingToolSet {

    private final String sessionId;
    private final BriefingSession session;
    private final Event event;
    private final BriefingAccumulatedData accumulated;
    private final BriefingRepositoryPort briefingRepository;
    private final SimpMessagingTemplate messaging;

    public BriefingToolSet(
            String sessionId,
            BriefingSession session,
            Event event,
            BriefingAccumulatedData accumulated,
            BriefingRepositoryPort briefingRepository,
            SimpMessagingTemplate messaging
    ) {
        this.sessionId = sessionId;
        this.session = session;
        this.event = event;
        this.accumulated = accumulated;
        this.briefingRepository = briefingRepository;
        this.messaging = messaging;
    }

    @Tool("Retorna o contexto do evento para personalizar a conversa com o cliente")
    public String getEventContext() {
        return String.format(
                "Evento: %s | Cliente: %s | Tipo: %s | Data: %s | Info adicional: %s",
                event.getName(),
                event.getClientName(),
                event.getType() != null ? event.getType() : "não informado",
                event.getEventDate() != null ? event.getEventDate().toString() : "não informada",
                event.getAdditionalInfo() != null ? event.getAdditionalInfo().toString() : "nenhuma"
        );
    }

    @Tool("Salva a vibe/atmosfera geral que o cliente deseja para o evento")
    public String saveVibe(@P("Descrição da vibe") String description) {
        accumulated.setVibe(description);
        return "Vibe registrada: " + description;
    }

    @Tool("Salva os estilos musicais preferidos pelo cliente")
    public String savePreferredStyles(@P("Lista de estilos musicais preferidos separados por vírgula") String styles) {
        List.of(styles.split(",")).stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .forEach(accumulated.getPreferredStyles()::add);
        return "Estilos preferidos registrados: " + styles;
    }

    @Tool("Salva os estilos musicais que devem ser evitados")
    public String saveForbiddenStyles(@P("Lista de estilos musicais proibidos separados por vírgula") String styles) {
        List.of(styles.split(",")).stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .forEach(accumulated.getForbiddenStyles()::add);
        return "Estilos proibidos registrados: " + styles;
    }

    @Tool("Registra uma música obrigatória que deve ser tocada no evento")
    public String saveRequiredSong(
            @P("Título da música") String title,
            @P("Nome do artista") String artist,
            @P("Momento do evento em que deve ser tocada (opcional)") String moment,
            @P("Observações adicionais (opcional)") String notes
    ) {
        accumulated.getRequiredSongs().add(Map.of(
                "title", title,
                "artist", artist,
                "moment", moment != null ? moment : "",
                "notes", notes != null ? notes : ""
        ));
        return "Música obrigatória registrada: " + title + " - " + artist;
    }

    @Tool("Registra uma música ou artista que não deve ser tocado no evento")
    public String saveForbiddenSong(
            @P("Título da música ou nome do artista a ser evitado") String titleOrArtist,
            @P("Motivo (opcional)") String reason
    ) {
        accumulated.getForbiddenSongs().add(Map.of(
                "titleOrArtist", titleOrArtist,
                "reason", reason != null ? reason : ""
        ));
        return "Música/artista proibido registrado: " + titleOrArtist;
    }

    @Tool("Registra um momento específico do evento com suas características musicais")
    public String saveEventMoment(
            @P("Nome do momento (ex: entrada dos noivos, primeira dança)") String name,
            @P("Vibe desejada para este momento") String vibe,
            @P("Estilos musicais sugeridos para este momento") String styles,
            @P("Observações adicionais (opcional)") String observation
    ) {
        accumulated.getMoments().add(Map.of(
                "name", name,
                "vibe", vibe,
                "styles", styles,
                "observation", observation != null ? observation : ""
        ));
        return "Momento registrado: " + name;
    }

    @Tool("Finaliza o briefing após confirmar com o cliente. Usa todos os dados coletados para gerar o resultado estruturado.")
    public String finalizeBriefing(
            @P("Resumo completo do briefing musical") String summary,
            @P("Perfil do convidado baseado na conversa") String guestProfile
    ) {
        BriefingResult result = new BriefingResult(
                event,
                summary,
                guestProfile,
                accumulated.getVibe(),
                accumulated.getPreferredStyles(),
                accumulated.getForbiddenStyles(),
                accumulated.getRequiredSongs(),
                accumulated.getForbiddenSongs(),
                accumulated.getMoments()
        );
        briefingRepository.saveResult(result);

        session.complete();
        briefingRepository.saveSession(session);

        briefingRepository.saveMessage(new BriefingMessage(session, MessageRole.ASSISTANT,
                "[BRIEFING_COMPLETE] " + summary));

        messaging.convertAndSend(
                "/topic/briefing/" + sessionId,
                ChatStreamMessage.briefingComplete(sessionId)
        );

        return "Briefing finalizado com sucesso. Resultado salvo para o DJ " + event.getDj().getArtisticName() + ".";
    }

    public BriefingSession getSession() {
        return session;
    }

    public BriefingAccumulatedData getAccumulated() {
        return accumulated;
    }
}
