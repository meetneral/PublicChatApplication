package com.teemlaren.chatApplication.controller;


import com.teemlaren.chatApplication.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal; // Import Principal
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // A simple in-memory map to store active users and their session IDs
    // In a real application, you might use a more robust presence management system
    private final ConcurrentMap<String, String> activeUsers = new ConcurrentHashMap<>();


    /**
     * Handles messages sent to /app/chat.sendMessage.
     * This method receives a chat message from a client and broadcasts it to all subscribers.
     * @param chatMessage The message received from the client.
     * @param principal The authenticated user's principal.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // Set the sender from the authenticated principal
        if (principal != null) {
            chatMessage.setSender(principal.getName());
        } else {
            // Fallback or error handling if principal is null (shouldn't happen with proper auth)
            chatMessage.setSender("anonymous");
        }
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    /**
     * Handles messages sent to /app/chat.addUser when a new user joins.
     * This method adds the user to active users and broadcasts a JOIN message.
     * @param chatMessage The join message from the client.
     * @param headerAccessor Provides access to STOMP message headers.
     * @param principal The authenticated user's principal.
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String username = null;
        if (principal != null) {
            username = principal.getName();
            // Store the username in the WebSocket session attributes if not already there
            // This is useful for the disconnect listener.
            if (headerAccessor.getSessionAttributes() != null) {
                headerAccessor.getSessionAttributes().put("username", username);
            }
        } else {
            // This case should ideally not be reached if authentication is enforced
            username = "anonymous";
        }

        chatMessage.setSender(username);
        chatMessage.setType(ChatMessage.MessageType.JOIN);

        // Add user to active users map
        if (username != null && headerAccessor.getSessionId() != null) {
            activeUsers.put(username, headerAccessor.getSessionId());
        }

        // Broadcast the join message
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}
