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
