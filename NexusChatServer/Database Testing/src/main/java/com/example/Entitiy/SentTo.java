package com.example.Entitiy;

import jakarta.persistence.*;

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
        this.id = new SentToId(message.getMessage_id(), receiver.getReceiverId());
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
}
