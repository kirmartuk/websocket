package com.martyuk.websocket.dto;

public class PrinterClientCommunicationDTO {
    private String type;
    private String massage;

    public PrinterClientCommunicationDTO() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
