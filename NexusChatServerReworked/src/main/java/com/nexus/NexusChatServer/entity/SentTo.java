package com.nexus.NexusChatServer.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "sent_to")
public class SentTo {

    @EmbeddedId
    private SentToId id;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @MapsId("receiverId")
    @JoinColumn(name = "receiver_id")
    private MessageDelivery receiver;

    @Column(name = "stat", nullable = false)
    private String stat = "PENDING"; // "PENDING" or "DELIVERED"

    public SentTo() {
    }

    public SentTo(Message message, MessageDelivery receiver) {
        this.message = message;
        this.receiver = receiver;
        this.id = new SentToId(message.getmId(), receiver.getReceiverId());
    }

    public SentToId getId() {
        return id;
    }

    public void setId(SentToId id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageDelivery getReceiver() {
        return receiver;
    }

    public void setReceiver(MessageDelivery receiver) {
        this.receiver = receiver;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SentTo sentTo = (SentTo) o;
        return id != null && id.equals(sentTo.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
