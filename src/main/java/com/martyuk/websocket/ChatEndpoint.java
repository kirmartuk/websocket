package com.martyuk.websocket;

import com.martyuk.websocket.dto.Message;
import com.martyuk.websocket.dto.Test;
import com.martyuk.websocket.jsonEncDec.decoders.MessageDecoder;
import com.martyuk.websocket.jsonEncDec.decoders.PrinterClientCommunicationDecoder;
import com.martyuk.websocket.jsonEncDec.decoders.TestDecoder;
import com.martyuk.websocket.jsonEncDec.encoders.MessageEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;




@ServerEndpoint(value="/chat/{id}",decoders = { TestDecoder.class, MessageDecoder.class, PrinterClientCommunicationDecoder.class},
        encoders = MessageEncoder.class )
public class ChatEndpoint {
    static String file;
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
        System.out.println(session.getMaxBinaryMessageBufferSize());
        session.setMaxBinaryMessageBufferSize(2000000);
        System.out.println(session.getMaxBinaryMessageBufferSize());


        users.put(session.getId(),username);


    }

    @OnMessage
    public void onMessage(Session session, Test message, @PathParam("id") String id)
            throws IOException, EncodeException {
        System.out.println(message.getSender()+" "+message.getMessage());
        file = message.getMessage();
       // System.out.println(file);
        broadcast(message, "text",id);
    }

    @OnMessage
    public void processUpload(byte[] imageData, boolean last, Session session,@PathParam("id") String id) throws IOException, EncodeException {
        ByteBuffer msg = ByteBuffer.wrap(imageData);
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
        System.out.println(throwable.getMessage());
        System.out.println(throwable.getCause());
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
                                    Test message = (Test) object;
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