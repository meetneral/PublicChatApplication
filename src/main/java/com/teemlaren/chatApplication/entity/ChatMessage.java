package com.teemlaren.chatApplication.entity;
/*import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation for getters, setters, equals, hashCode, toString
@NoArgsConstructor // Lombok annotation for no-arg constructor
@AllArgsConstructor // Lombok annotation for all-args constructor
public class ChatMessage {
    private String sender;
    private String content;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}*/

import java.util.Objects; // For Objects.hash and Objects.equals

public class ChatMessage {
    private String sender;
    private String content;
    private MessageType type;

    // No-argument constructor
    public ChatMessage() {
    }

    // All-argument constructor
    public ChatMessage(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    // Getter for sender
    public String getSender() {
        return sender;
    }

    // Setter for sender
    public void setSender(String sender) {
        this.sender = sender;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }

    // Getter for type
    public MessageType getType() {
        return type;
    }

    // Setter for type
    public void setType(MessageType type) { // Corrected: removed 'void' before setType
        this.type = type;
    }

    // Enum for message types
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    // Override equals method (optional, but good practice for data classes)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(sender, that.sender) &&
                Objects.equals(content, that.content) &&
                type == that.type;
    }

    // Override hashCode method (optional, but good practice for data classes)
    @Override
    public int hashCode() {
        return Objects.hash(sender, content, type);
    }

    // Override toString method
    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
/*<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring Boot Chat</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f7f6; color: #333; }
        .chat-container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .messages {
            height: 400px;
            overflow-y: auto;
            padding: 15px;
            border-bottom: 1px solid #eee;
            background-color: #e9ecef;
        }
        .message {
            margin-bottom: 10px;
            padding: 8px 12px;
            border-radius: 5px;
            word-wrap: break-word;
        }
        .message.self {
            background-color: #dcf8c6;
            text-align: right;
            margin-left: 20%;
        }
        .message.other {
            background-color: #f1f0f0;
            text-align: left;
            margin-right: 20%;
        }
        .message.join-leave {
            text-align: center;
            font-style: italic;
            color: #666;
        }
        .input-area {
            display: flex;
            padding: 15px;
            background-color: #f8f9fa;
        }
        .input-area input[type="text"] {
            flex-grow: 1;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            margin-right: 10px;
            font-size: 16px;
        }
        .input-area button {
            padding: 10px 15px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        .input-area button:hover {
            background-color: #0056b3;
        }
        .username-input {
            padding: 15px;
            background-color: #f8f9fa;
            border-bottom: 1px solid #eee;
            text-align: center;
        }
        .username-input input {
            padding: 8px;
            margin-right: 5px;
            border: 1px solid #ced4da;
            border-radius: 5px;
        }
        .username-input button {
            padding: 8px 12px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .username-input button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="chat-container">
        <div class="username-input" id="usernameInputArea">
            <input type="text" id="usernameInput" placeholder="Enter your username">
            <button id="connectButton">Connect</button>
        </div>
        <div class="messages" id="messages">
            <!-- Chat messages will appear here -->
        </div>
        <div class="input-area">
            <input type="text" id="messageInput" placeholder="Type your message..." disabled>
            <button id="sendMessageButton" disabled>Send</button>
        </div>
    </div>

    <script>
        let stompClient = null;
        let username = null;

        const messagesDiv = document.getElementById('messages');
        const messageInput = document.getElementById('messageInput');
        const sendMessageButton = document.getElementById('sendMessageButton');
        const usernameInput = document.getElementById('usernameInput');
        const connectButton = document.getElementById('connectButton');
        const usernameInputArea = document.getElementById('usernameInputArea');

        connectButton.addEventListener('click', connect);
        sendMessageButton.addEventListener('click', sendMessage);
        messageInput.addEventListener('keypress', (event) => {
            if (event.key === 'Enter') {
                sendMessage();
            }
        });

        function connect() {
            username = usernameInput.value.trim();
            if (username) {
                usernameInputArea.style.display = 'none';
                messageInput.disabled = false;
                sendMessageButton.disabled = false;

                // Use SockJS for fallback if WebSockets are not supported
                const socket = new SockJS('/ws');
                stompClient = Stomp.over(socket);

                stompClient.connect({}, onConnected, onError);
            } else {
                alert('Please enter a username!');
            }
        }

        function onConnected() {
            console.log('Connected to WebSocket');
            // Subscribe to the public topic
            stompClient.subscribe('/topic/public', onMessageReceived);

            // Tell the server your username
            stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
        }

        function onError(error) {
            console.error('Could not connect to WebSocket server. Error: ' + error);
            messagesDiv.innerHTML += `<div class="message join-leave">Could not connect to chat server. Please try again later.</div>`;
            usernameInputArea.style.display = 'block';
            messageInput.disabled = true;
            sendMessageButton.disabled = true;
        }

        function sendMessage() {
            const messageContent = messageInput.value.trim();
            if (messageContent && stompClient) {
                const chatMessage = {
                    sender: username,
                    content: messageContent,
                    type: 'CHAT'
                };
                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
                messageInput.value = ''; // Clear input
            }
        }

        function onMessageReceived(payload) {
            const message = JSON.parse(payload.body);
            const messageElement = document.createElement('div');
            messageElement.classList.add('message');

            if (message.type === 'JOIN') {
                messageElement.classList.add('join-leave');
                messageElement.textContent = message.sender + ' joined!';
            } else if (message.type === 'LEAVE') {
                messageElement.classList.add('join-leave');
                messageElement.textContent = message.sender + ' left!';
            } else {
                messageElement.classList.add(message.sender === username ? 'self' : 'other');
                const senderSpan = document.createElement('span');
                senderSpan.style.fontWeight = 'bold';
                senderSpan.textContent = message.sender + ': ';
                messageElement.appendChild(senderSpan);
                messageElement.appendChild(document.createTextNode(message.content));
            }
            messagesDiv.appendChild(messageElement);
            messagesDiv.scrollTop = messagesDiv.scrollHeight; // Scroll to bottom
        }
    </script>
</body>
</html>
*/