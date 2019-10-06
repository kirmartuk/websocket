package com.martyuk.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;




@ServerEndpoint(value="/chat/{username}",decoders = MessageDecoder.class,
        encoders = MessageEncoder.class )
public class ChatEndpoint {

    private Session session;
    private static Set<ChatEndpoint> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String username) throws IOException {

        this.session = session;
        chatEndpoints.add(this);
        System.out.println(session.getId());
        users.put(session.getId(), username);


    }

    @OnMessage
    public void onMessage(Session session, Message message)
            throws IOException, EncodeException {
        System.out.println(message.getCommand());

        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {

        chatEndpoints.remove(this);

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {

        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message.getCommand());
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
