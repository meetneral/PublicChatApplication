package com.teemlaren.chatApplication.config; // Assuming it's in config package

import com.teemlaren.chatApplication.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal; // Import Principal
import java.util.Objects;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        // Get the Principal (authenticated user) from the event
        Principal principal = event.getUser();
        if (principal != null) {
            String username = principal.getName(); // Get username from Principal
            logger.info("User {} connected via WebSocket", username);

            // Optionally, send a join message here if not handled by /chat.addUser
            // Note: The client's /app/chat.addUser will also send a JOIN message,
            // so you might want to handle this deduplication or choose one place.
            // For now, let's keep it simple and rely on the client's /app/chat.addUser.
        } else {
            logger.warn("Unauthenticated WebSocket connection attempt.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // Get the Principal (authenticated user) from the event
        Principal principal = event.getUser();
        String username = null;
        if (principal != null) {
            username = principal.getName(); // Get username from Principal
        } else {
            // Fallback to session attributes if Principal is null (less common with JWT)
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
            if (headerAccessor.getSessionAttributes() != null) {
                username = (String) headerAccessor.getSessionAttributes().get("username");
            }
        }

        if (username != null) {
            logger.info("User Disconnected: {}", username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            // Notify all subscribers that a user has left
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        } else {
            logger.warn("Disconnected an unknown user (username not found in Principal or session attributes).");
        }
    }
}
