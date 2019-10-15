package com.martyuk.websocket.jsonEncDec.decoders;

import com.google.gson.Gson;
import com.martyuk.websocket.dto.MyWord;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MyWordDecoder implements Decoder.Text<MyWord> {

    private static Gson gson = new Gson();


    @Override
    public MyWord decode(String s) throws DecodeException {
        MyWord object= gson.fromJson(s,MyWord.class);
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