package com.martyuk.websocket.jsonEncDec.decoders;

import com.google.gson.Gson;
import com.martyuk.websocket.dto.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

    private static Gson gson = new Gson();


    @Override
    public Message decode(String s) throws DecodeException {
        Message object= gson.fromJson(s, Message.class);
        return object;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
