package io.github.ktpm.bluemoonmanagement.chat;

import io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ChatClientManager {
    private final WebSocketStompClient stompClient;
    private StompSession session;
    private final String url;

    public ChatClientManager(String websocketUrl) {
        this.url = websocketUrl;
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        // configure Jackson to support Java 8 date/time types (Instant)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);
        this.stompClient.setMessageConverter(converter);
    }

    public void connect(String connectToken, Consumer<ChatMessage> messageConsumer, Runnable onConnected, Consumer<Throwable> onError) {
        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession stompSession, StompHeaders connectedHeaders) {
                session = stompSession;
                // subscribe to public topic
                session.subscribe("/topic/public", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        if (payload instanceof ChatMessage) {
                            messageConsumer.accept((ChatMessage) payload);
                        }
                    }
                });
                if (onConnected != null) onConnected.run();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                if (onError != null) onError.accept(exception);
            }
        };

        try {
            stompClient.connect(url, handler).get();
        } catch (ExecutionException | InterruptedException firstEx) {
            // If initial raw connect failed (common when server registered SockJS at /ws-chat/**)
            // try connecting to the SockJS websocket transport path.
            if (!url.endsWith("/websocket")) {
                String alt = url.endsWith("/") ? url + "websocket" : url + "/websocket";
                try {
                    stompClient.connect(alt, handler).get();
                } catch (ExecutionException | InterruptedException secondEx) {
                    if (onError != null) onError.accept(firstEx.getCause() != null ? firstEx.getCause() : firstEx);
                }
            } else {
                if (onError != null) onError.accept(firstEx.getCause() != null ? firstEx.getCause() : firstEx);
            }
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        stompClient.stop();
    }

    public void sendMessage(ChatMessage msg) {
        if (session != null && session.isConnected()) {
            session.send("/app/chat.send", msg);
        }
    }
}


