package com.martyuk.websocket.jsonEncDec.encoders;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Object> {

    private static Gson gson = new Gson();


    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }

    @Override
    public String encode(Object object) throws EncodeException {
        String json = gson.toJson(object);
        return json;
    }
}
