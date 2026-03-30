package com.example.Entitiy;

import com.example.Entitiy.SentTo;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message_delivery")
public class MessageDelivery {

    @Id
    @Column(name = "receiver_id")
    private String receiverId;


    // Messages delivered to this receiver
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SentTo> messages = new HashSet<>();

    public MessageDelivery() {}

    public MessageDelivery(String receiverId, String stat) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Set<SentTo> getMessages() {
        return messages;
    }

    public void setMessages(Set<SentTo> messages) {
        this.messages = messages;
    }
}