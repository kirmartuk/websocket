package com.martyuk.websocket.jsonEncDec.decoders;

import com.google.gson.Gson;
import com.martyuk.websocket.dto.Test;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class TestDecoder implements Decoder.Text<Test> {

    private static Gson gson = new Gson();


    @Override
    public Test decode(String s) throws DecodeException {
        Test object= gson.fromJson(s,Test.class);
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
