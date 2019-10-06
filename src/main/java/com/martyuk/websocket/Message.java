package com.martyuk.websocket;

public class Message {
    String command;

    public Message() {
    }

    public Message(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
