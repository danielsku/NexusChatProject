package com.nexus.NexusChatServer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SentToId implements Serializable {

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "receiver_id")
    private String receiverId;

    public SentToId() {
    }

    public SentToId(String message_id, String receiver_id) {
        this.messageId = message_id;
        this.receiverId = receiver_id;
    }

    public String getMessage_id() {
        return messageId;
    }

    public void setMessage_id(String message_id) {
        this.messageId = message_id;
    }

    public String getReceiver_id() {
        return receiverId;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiverId = receiver_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SentToId))
            return false;
        SentToId that = (SentToId) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(receiverId, that.receiverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, receiverId);
    }
}
