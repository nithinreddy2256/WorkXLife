package com.workxlife.recommendation_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenAiRequest {
    private String model;
    private List<OpenAiMessage> messages;
    private double temperature = 0.7;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<OpenAiMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<OpenAiMessage> messages) {
        this.messages = messages;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
