package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.ai.BriefingAccumulatedData;
import br.com.djdesk.service.adapter.in.ai.BriefingAgentGraph;
import br.com.djdesk.service.adapter.in.ai.BriefingToolSet;
import br.com.djdesk.service.adapter.in.websocket.dto.ChatStreamMessage;
import br.com.djdesk.service.domain.enums.MessageRole;
import br.com.djdesk.service.domain.model.AgentConfig;
import br.com.djdesk.service.domain.model.BriefingMessage;
import br.com.djdesk.service.domain.model.BriefingSession;
import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.in.StartBriefingUseCase;
import br.com.djdesk.service.domain.port.out.AgentConfigRepositoryPort;
import br.com.djdesk.service.domain.port.out.BriefingRepositoryPort;
import br.com.djdesk.service.domain.port.out.EventRepositoryPort;
import br.com.djdesk.service.shared.enums.ErrorCode;
import br.com.djdesk.service.shared.exception.ResourceNotFoundException;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StartBriefingService implements StartBriefingUseCase {

    private static final Logger log = LoggerFactory.getLogger(StartBriefingService.class);
    private static final String AGENT_CONFIG_NAME = "briefing_assistant";

    private final EventRepositoryPort eventRepositoryPort;
    private final BriefingRepositoryPort briefingRepositoryPort;
    private final AgentConfigRepositoryPort agentConfigRepositoryPort;
    private final OpenAiStreamingChatModel streamingModel;
    private final SimpMessagingTemplate messaging;

    public StartBriefingService(
            EventRepositoryPort eventRepositoryPort,
            BriefingRepositoryPort briefingRepositoryPort,
            AgentConfigRepositoryPort agentConfigRepositoryPort,
            OpenAiStreamingChatModel streamingModel,
            SimpMessagingTemplate messaging
    ) {
        this.eventRepositoryPort = eventRepositoryPort;
        this.briefingRepositoryPort = briefingRepositoryPort;
        this.agentConfigRepositoryPort = agentConfigRepositoryPort;
        this.streamingModel = streamingModel;
        this.messaging = messaging;
    }

    @Override
    @Transactional
    public String start(UUID publicToken) {
        log.info("Starting briefing [publicToken={}]", publicToken);

        Event event = eventRepositoryPort.findByPublicLink(publicToken)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND, "Event not found: " + publicToken));

        AgentConfig config = agentConfigRepositoryPort.findByName(AGENT_CONFIG_NAME)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.AGENT_CONFIG_NOT_FOUND, "Agent config not found: " + AGENT_CONFIG_NAME));

        BriefingSession session = briefingRepositoryPort.saveSession(new BriefingSession(event));
        String sessionId = session.getSlug().toString();

        messaging.convertAndSend("/topic/briefing/session/" + publicToken,
                ChatStreamMessage.sessionStarted(sessionId));

        streamGreetingAsync(sessionId, session, event, config);

        return sessionId;
    }

    @Async
    protected void streamGreetingAsync(
            String sessionId,
            BriefingSession session,
            Event event,
            AgentConfig config
    ) {
        try {
            var accumulated = new BriefingAccumulatedData();
            var toolSet = new BriefingToolSet(sessionId, session, event, accumulated, briefingRepositoryPort, messaging);
            var graph = new BriefingAgentGraph(streamingModel, toolSet, messaging, sessionId);

            String greeting = String.format(
                    "Olá! Sou o assistente musical do %s. Vou te ajudar a criar o briefing musical para sua festa \"%s\". Vamos começar?",
                    event.getDj().getArtisticName(),
                    event.getName()
            );

            briefingRepositoryPort.saveMessage(new BriefingMessage(session, MessageRole.ASSISTANT, greeting));

            var systemMsg = SystemMessage.from(config.getSystemPrompt());
            var greetingMsg = dev.langchain4j.data.message.AiMessage.from(greeting);
            var kickoffMsg = UserMessage.from("Pode começar! Qual é a primeira pergunta?");

            for (var out : graph.getCompiled().stream(
                    Map.of("messages", List.of(systemMsg, greetingMsg, kickoffMsg)))) {
                log.debug("Graph output: {}", out);
            }

            messaging.convertAndSend("/topic/briefing/" + sessionId,
                    ChatStreamMessage.done(sessionId));

        } catch (Exception e) {
            log.error("Error in greeting stream [sessionId={}]", sessionId, e);
            messaging.convertAndSend("/topic/briefing/" + sessionId,
                    ChatStreamMessage.error(sessionId, "Erro ao iniciar briefing"));
        }
    }
}
