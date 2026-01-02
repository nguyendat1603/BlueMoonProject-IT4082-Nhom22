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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ChatClientManager {
    private final WebSocketStompClient stompClient;
    private StompSession session;
    private final String url;

    // Heartbeat and connection monitoring
    private ScheduledExecutorService heartbeatExecutor;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private Consumer<ChatMessage> currentMessageConsumer;
    private Runnable currentOnConnected;
    private Consumer<Throwable> currentOnError;
    private volatile long lastHeartbeatReceived = System.currentTimeMillis();

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
        // Store callbacks for reconnection
        this.currentMessageConsumer = messageConsumer;
        this.currentOnConnected = onConnected;
        this.currentOnError = onError;

        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession stompSession, StompHeaders connectedHeaders) {
                session = stompSession;
                isConnected.set(true);
                lastHeartbeatReceived = System.currentTimeMillis();

                // Start heartbeat monitoring
                startHeartbeatMonitoring();
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
                // subscribe to history topic (array payload)
                session.subscribe("/topic/history", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatMessage[].class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        if (payload instanceof ChatMessage[]) {
                            ChatMessage[] msgs = (ChatMessage[]) payload;
                            // deliver history messages in order
                            for (ChatMessage m : msgs) {
                                messageConsumer.accept(m);
                            }
                        }
                    }
                });
                System.out.println("CHAT: WebSocket connected successfully to " + url);
                if (onConnected != null) onConnected.run();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("CHAT: WebSocket transport error: " + exception.getMessage());
                isConnected.set(false);
                if (onError != null) onError.accept(exception);
                // Note: Reconnection will be handled by heartbeat monitoring
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
        isConnected.set(false);
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdown();
            try {
                if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    heartbeatExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                heartbeatExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        stompClient.stop();
        System.out.println("CHAT: Disconnected and heartbeat stopped");
    }

    public void sendMessage(ChatMessage msg) {
        if (session != null && session.isConnected()) {
            session.send("/app/chat.send", msg);
        }
    }

    public void sendToDestination(String destination, Object payload) {
        if (session != null && session.isConnected()) {
            try {
                session.send(destination, payload == null ? "" : payload);
            } catch (Exception e) {
                System.err.println("CHAT: Failed to send to " + destination + ": " + e.getMessage());
            }
        }
    }

    private void startHeartbeatMonitoring() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdown();
        }

        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ChatHeartbeatMonitor");
            t.setDaemon(true);
            return t;
        });

        System.out.println("CHAT: Heartbeat executor created, scheduling ping task...");

        heartbeatExecutor.scheduleAtFixedRate(() -> {
            System.out.println("CHAT: Heartbeat task running...");
            try {
                if (!isConnected.get()) {
                    System.out.println("CHAT: Connection lost, attempting to reconnect...");
                    reconnect();
                    return;
                }

                // Send a simple ping-like message to keep connection alive
                if (session != null && session.isConnected()) {
                    try {
                        // Use dedicated ping endpoint instead of history
                        session.send("/app/chat.ping", "");
                    } catch (Exception e) {
                        System.err.println("CHAT: Heartbeat send failed: " + e.getMessage());
                        isConnected.set(false);
                        reconnect();
                    }
                } else {
                    System.out.println("CHAT: Session is null or not connected");
                }
            } catch (Exception e) {
                System.err.println("CHAT: Heartbeat monitoring error: " + e.getMessage());
                e.printStackTrace();
            }
        }, 60, 60, TimeUnit.SECONDS); // Send ping every 60 seconds

        System.out.println("CHAT: Heartbeat monitoring started and ping task scheduled");
    }

    private void reconnect() {
        try {
            // Clean up current session
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            isConnected.set(false);

            System.out.println("CHAT: Waiting 2 seconds before reconnect...");
            Thread.sleep(2000); // Wait before reconnect

            System.out.println("CHAT: Attempting to reconnect...");
            connect(null, currentMessageConsumer, currentOnConnected, currentOnError);

        } catch (Exception e) {
            System.err.println("CHAT: Reconnection failed: " + e.getMessage());
            if (currentOnError != null) {
                currentOnError.accept(e);
            }
        }
    }

    public boolean isConnected() {
        return isConnected.get();
    }
}


