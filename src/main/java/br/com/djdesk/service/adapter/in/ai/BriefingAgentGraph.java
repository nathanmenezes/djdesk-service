package br.com.djdesk.service.adapter.in.ai;

import br.com.djdesk.service.adapter.in.websocket.dto.ChatStreamMessage;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.action.Command;
import org.bsc.langgraph4j.langchain4j.generators.StreamingChatGenerator;
import org.bsc.langgraph4j.langchain4j.serializer.std.ChatMesssageSerializer;
import org.bsc.langgraph4j.langchain4j.serializer.std.ToolExecutionRequestSerializer;
import org.bsc.langgraph4j.langchain4j.tool.LC4jToolService;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

public class BriefingAgentGraph {

    private static final Logger log = LoggerFactory.getLogger(BriefingAgentGraph.class);

    private final org.bsc.langgraph4j.CompiledGraph<MessagesState<ChatMessage>> compiled;

    public BriefingAgentGraph(
            OpenAiStreamingChatModel streamingModel,
            BriefingToolSet toolSet,
            SimpMessagingTemplate messaging,
            String sessionId
    ) throws Exception {
        var tools = LC4jToolService.builder()
                .toolsFromObject(toolSet)
                .build();

        var stateSerializer = new ObjectStreamStateSerializer<MessagesState<ChatMessage>>(MessagesState::new);
        stateSerializer.mapper()
                .register(ToolExecutionRequest.class, new ToolExecutionRequestSerializer())
                .register(ChatMessage.class, new ChatMesssageSerializer());

        this.compiled = new StateGraph<>(MessagesState.SCHEMA, stateSerializer)
                .addNode("agent", node_async(state -> {
                    log.debug("Agent node — {} messages", state.messages().size());

                    var generator = StreamingChatGenerator
                            .<MessagesState<ChatMessage>>builder()
                            .mapResult(r -> Map.of("messages", r.aiMessage()))
                            .startingNode("agent")
                            .startingState(state)
                            .build();

                    var innerHandler = generator.handler();
                    StreamingChatResponseHandler stompHandler = new StreamingChatResponseHandler() {
                        @Override
                        public void onPartialResponse(String token) {
                            messaging.convertAndSend(
                                    "/topic/briefing/" + sessionId,
                                    ChatStreamMessage.chunk(sessionId, token)
                            );
                            innerHandler.onPartialResponse(token);
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse response) {
                            innerHandler.onCompleteResponse(response);
                        }

                        @Override
                        public void onError(Throwable error) {
                            messaging.convertAndSend(
                                    "/topic/briefing/" + sessionId,
                                    ChatStreamMessage.error(sessionId, error.getMessage())
                            );
                            innerHandler.onError(error);
                        }
                    };

                    var params = ChatRequestParameters.builder()
                            .toolSpecifications(tools.toolSpecifications())
                            .build();
                    var request = ChatRequest.builder()
                            .parameters(params)
                            .messages(state.messages())
                            .build();

                    streamingModel.chat(request, stompHandler);
                    return Map.of("messages", generator);
                }))
                .addNode("tools", (AsyncNodeAction<MessagesState<ChatMessage>>) state -> {
                    log.debug("Tools node — {} messages", state.messages().size());

                    var lastMessage = state.lastMessage()
                            .orElseThrow(() -> new IllegalStateException("last message not found"));

                    if (lastMessage instanceof AiMessage aiMessage) {
                        return tools.execute(
                                aiMessage.toolExecutionRequests(),
                                InvocationContext.builder()
                                        .invocationParameters(InvocationParameters.from(Map.of()))
                                        .build(),
                                "messages"
                        ).thenApply(Command::update);
                    }

                    return CompletableFuture.failedFuture(
                            new IllegalStateException("Expected AiMessage in tools node"));
                })
                .addEdge(START, "agent")
                .addConditionalEdges("agent",
                        edge_async(rawState -> {
                            @SuppressWarnings("unchecked")
                            var state = (MessagesState<ChatMessage>) rawState;
                            var last = state.lastMessage()
                                    .orElseThrow(() -> new IllegalStateException("last message not found"));
                            if (last instanceof AiMessage msg && msg.hasToolExecutionRequests()) {
                                return "next";
                            }
                            return "exit";
                        }),
                        Map.of("next", "tools", "exit", END)
                )
                .addEdge("tools", "agent")
                .compile();
    }

    public org.bsc.langgraph4j.CompiledGraph<MessagesState<ChatMessage>> getCompiled() {
        return compiled;
    }
}

