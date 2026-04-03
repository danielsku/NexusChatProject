package com.nexus.NexusChatServer.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "message_delivery")
public class MessageDelivery {

    @Id
    @Column(name = "receiver_id")
    private String receiverId;


    // Messages delivered to this receiver
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SentTo> messages = new ArrayList<>();

    public MessageDelivery() {}

    public MessageDelivery(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public List<SentTo> getMessages() {
        return messages;
    }

    public void setMessages(List<SentTo> messages) {
        this.messages = messages;
    }
}