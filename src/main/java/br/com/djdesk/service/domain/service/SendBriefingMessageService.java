package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.ai.BriefingAccumulatedData;
import br.com.djdesk.service.adapter.in.ai.BriefingAgentGraph;
import br.com.djdesk.service.adapter.in.ai.BriefingToolSet;
import br.com.djdesk.service.adapter.in.websocket.dto.ChatStreamMessage;
import br.com.djdesk.service.domain.enums.MessageRole;
import br.com.djdesk.service.domain.model.AgentConfig;
import br.com.djdesk.service.domain.model.BriefingMessage;
import br.com.djdesk.service.domain.model.BriefingSession;
import br.com.djdesk.service.domain.port.in.SendBriefingMessageUseCase;
import br.com.djdesk.service.domain.port.out.AgentConfigRepositoryPort;
import br.com.djdesk.service.domain.port.out.BriefingRepositoryPort;
import br.com.djdesk.service.shared.enums.ErrorCode;
import br.com.djdesk.service.shared.exception.ResourceNotFoundException;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SendBriefingMessageService implements SendBriefingMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendBriefingMessageService.class);
    private static final String AGENT_CONFIG_NAME = "briefing_assistant";

    private final BriefingRepositoryPort briefingRepositoryPort;
    private final AgentConfigRepositoryPort agentConfigRepositoryPort;
    private final OpenAiStreamingChatModel streamingModel;
    private final SimpMessagingTemplate messaging;

    public SendBriefingMessageService(
            BriefingRepositoryPort briefingRepositoryPort,
            AgentConfigRepositoryPort agentConfigRepositoryPort,
            OpenAiStreamingChatModel streamingModel,
            SimpMessagingTemplate messaging
    ) {
        this.briefingRepositoryPort = briefingRepositoryPort;
        this.agentConfigRepositoryPort = agentConfigRepositoryPort;
        this.streamingModel = streamingModel;
        this.messaging = messaging;
    }

    @Override
    @Transactional
    public void send(String sessionId, String message) {
        log.info("Sending message [sessionId={}]", sessionId);

        BriefingSession session = briefingRepositoryPort.findSessionBySlug(UUID.fromString(sessionId))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRIEFING_SESSION_NOT_FOUND, "Session not found: " + sessionId));

        AgentConfig config = agentConfigRepositoryPort.findByName(AGENT_CONFIG_NAME)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.AGENT_CONFIG_NOT_FOUND, "Agent config not found: " + AGENT_CONFIG_NAME));

        briefingRepositoryPort.saveMessage(new BriefingMessage(session, MessageRole.USER, message));

        processMessageAsync(sessionId, session, config, message);
    }

    @Async
    protected void processMessageAsync(
            String sessionId,
            BriefingSession session,
            AgentConfig config,
            String userMessage
    ) {
        try {
            var history = briefingRepositoryPort.findMessagesBySessionSlug(session.getSlug());
            var accumulated = new BriefingAccumulatedData();
            var toolSet = new BriefingToolSet(sessionId, session, session.getEvent(), accumulated,
                    briefingRepositoryPort, messaging);
            var graph = new BriefingAgentGraph(streamingModel, toolSet, messaging, sessionId);

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from(config.getSystemPrompt()));

            for (var msg : history) {
                if (msg.getRole() == br.com.djdesk.service.domain.enums.MessageRole.USER) {
                    messages.add(UserMessage.from(msg.getContent()));
                } else {
                    messages.add(AiMessage.from(msg.getContent()));
                }
            }

            for (var out : graph.getCompiled().stream(Map.of("messages", messages))) {
                log.debug("Graph output: {}", out);
            }

            messaging.convertAndSend("/topic/briefing/" + sessionId,
                    ChatStreamMessage.done(sessionId));

        } catch (Exception e) {
            log.error("Error processing message [sessionId={}]", sessionId, e);
            messaging.convertAndSend("/topic/briefing/" + sessionId,
                    ChatStreamMessage.error(sessionId, "Erro ao processar mensagem"));
        }
    }
}
