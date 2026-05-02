package br.com.djdesk.service.adapter.in.websocket;

import br.com.djdesk.service.adapter.in.websocket.dto.SendMessageRequest;
import br.com.djdesk.service.adapter.in.websocket.dto.StartBriefingRequest;
import br.com.djdesk.service.domain.port.in.SendBriefingMessageUseCase;
import br.com.djdesk.service.domain.port.in.StartBriefingUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BriefingWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(BriefingWebSocketController.class);

    private final StartBriefingUseCase startBriefingUseCase;
    private final SendBriefingMessageUseCase sendBriefingMessageUseCase;

    public BriefingWebSocketController(
            StartBriefingUseCase startBriefingUseCase,
            SendBriefingMessageUseCase sendBriefingMessageUseCase
    ) {
        this.startBriefingUseCase = startBriefingUseCase;
        this.sendBriefingMessageUseCase = sendBriefingMessageUseCase;
    }

    @MessageMapping("/briefing/start")
    public void start(StartBriefingRequest request) {
        log.info("WS start briefing [publicToken={}]", request.publicToken());
        startBriefingUseCase.start(request.publicToken());
    }

    @MessageMapping("/briefing/{sessionId}/message")
    public void sendMessage(
            @DestinationVariable String sessionId,
            SendMessageRequest request
    ) {
        log.info("WS message [sessionId={}]", sessionId);
        sendBriefingMessageUseCase.send(sessionId, request.message());
    }
}
