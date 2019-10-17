package com.martyuk.websocket.jsonEncDec.decoders;

import com.google.gson.Gson;
import com.martyuk.websocket.dto.Message;
import org.springframework.hateoas.PagedResources;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class PrinterClientCommunicationDecoder implements Decoder.Text<PrinterClientCommunicationDecoder> {

    private static Gson gson = new Gson();


    @Override
    public PrinterClientCommunicationDecoder decode(String s) throws DecodeException {
        PrinterClientCommunicationDecoder object= gson.fromJson(s,PrinterClientCommunicationDecoder.class);
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