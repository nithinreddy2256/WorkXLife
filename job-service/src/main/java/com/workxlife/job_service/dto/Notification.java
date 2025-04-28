package com.workxlife.job_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class Notification {
    private Long recipientId;
    private String recipientEmail;
    private String message;
    private String type; // "EMAIL" or "IN_APP"

    public Notification() {}

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
