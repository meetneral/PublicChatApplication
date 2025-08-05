package com.teemlaren.chatApplication.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

/**
 * Intercepts the WebSocket handshake to store the authenticated user's Principal
 * in the WebSocket session attributes. This allows the user's identity to be
 * accessible in WebSocket event listeners and message handlers.
 */
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Check if the user is authenticated (Principal is available)
        Principal principal = request.getPrincipal();
        if (principal != null) {
            // Store the username in the WebSocket session attributes
            // This makes the username available in WebSocketEventListener and ChatController
            attributes.put("username", principal.getName());
        }
        return true; // Continue the handshake
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No specific action needed after handshake for this purpose
    }
}

