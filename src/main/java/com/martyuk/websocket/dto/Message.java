package com.martyuk.websocket.dto;

public class Message {
    String sender;
    String message;

    @Override
    public String toString() {
        return "Test{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {
    }

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
