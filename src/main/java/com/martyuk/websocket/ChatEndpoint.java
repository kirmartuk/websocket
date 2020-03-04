package com.martyuk.websocket;

import com.martyuk.websocket.dto.Message;
import com.martyuk.websocket.jsonEncDec.decoders.MessageDecoder;
import com.martyuk.websocket.jsonEncDec.encoders.MessageEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint(value = "/chat/{id}", decoders = {MessageDecoder.class},
        encoders = MessageEncoder.class)
public class ChatEndpoint {
    private Session session;
    private static Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();
    private static Map<String, String> userFiles = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String username)
            throws IOException, EncodeException {
        this.session = session;
        chatEndpoints.add(this);
        System.out.println(session.getId());
        System.out.println(session.getMaxBinaryMessageBufferSize());
        session.setMaxBinaryMessageBufferSize(2000000);
        System.out.println(session.getMaxBinaryMessageBufferSize());
        users.put(session.getId(), username);
        if (userFiles.get(username) != null && !userFiles.get(username).equals("")) {
            String file = userFiles.get(username);
            Message message = new Message("server", file);
            session.getBasicRemote().sendObject(message);
        }
    }

    @OnMessage
    public void onMessage(Session session, Message message, @PathParam("id") String id)
            throws IOException, EncodeException {
        System.out.println(message.getSender() + " " + message.getMessage());
        userFiles.put(id, message.getMessage());
        broadcast(message, "text", id);
    }

    @OnMessage
    public void processUpload(byte[] imageData, boolean last, Session session, @PathParam("id") String id)
            throws IOException, EncodeException {
        ByteBuffer msg = ByteBuffer.wrap(imageData);
        broadcast(msg, "binary", id);
    }

    @OnClose
    public void onClose(Session session)
            throws IOException {
        System.out.println("close");
        chatEndpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println(throwable.getMessage());
        System.out.println(throwable.getCause());
        session.getAsyncRemote().sendText("Ошибка");
    }

    private static void broadcast(Object object, String type, String id)
            throws IOException, EncodeException {
        chatEndpoints.stream()
                .filter(chatEndpoint -> users.get(chatEndpoint.session.getId()).equals(id))
                .forEach(endpoint -> {
                    synchronized (endpoint) {
                        try {
                            switch (type) {
                                case ("text"):
                                    Message message = (Message) object;
                                    endpoint.session.getBasicRemote().
                                            sendObject(message);
                                    break;
                                case ("binary"):
                                    ByteBuffer byteBuffer = (ByteBuffer) object;
                                    endpoint.session.getAsyncRemote().sendBinary(byteBuffer);
                                    break;
                            }
                        } catch (IOException | EncodeException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
