package com.martyuk.websocket;

import com.martyuk.websocket.dto.Message;
import com.martyuk.websocket.dto.MyWord;
import com.martyuk.websocket.jsonEncDec.decoders.MessageDecoder;
import com.martyuk.websocket.jsonEncDec.decoders.MyWordDecoder;
import com.martyuk.websocket.jsonEncDec.encoders.MessageEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;




@ServerEndpoint(value="/chat/{id}",decoders = {MessageDecoder.class, MyWordDecoder.class},
        encoders = MessageEncoder.class )
public class ChatEndpoint {

    private Session session;
    private static Set<ChatEndpoint> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();


    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("id") String username) throws IOException {

        this.session = session;
        chatEndpoints.add(this);
        System.out.println(session.getId());
        users.put(session.getId(),username);


    }

    @OnMessage
    public void onMessage(Session session, Message message,@PathParam("id") String id)
            throws IOException, EncodeException {
        System.out.println(message.getCommand());
        System.out.println(id);

        broadcast(message, "text",id);
    }

    @OnMessage
    public void processUpload(ByteBuffer msg, boolean last, Session session,@PathParam("id") String id) throws IOException, EncodeException {
       broadcast(msg,"binary",id);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("close");
        chatEndpoints.remove(this);

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        session.getAsyncRemote().sendText("Ошибка");
    }

    private static void broadcast(Object object, String type,String id)
            throws IOException, EncodeException {


        chatEndpoints.stream()
                .filter(chatEndpoint -> users.get(chatEndpoint.session.getId()).equals(id))
                .forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    switch (type){
                        case ("text"):
                            System.out.println(object);
                            Message message = (Message) object;
                            endpoint.session.getBasicRemote().
                                    sendObject(message.getCommand());
                            break;
                        case ("binary"):
                            ByteBuffer byteBuffer = (ByteBuffer) object;
                            endpoint.session.getBasicRemote().sendBinary(byteBuffer);
                            break;

                    }
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
