package com.nexus.NexusChatServer.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @Column(name="message_id")
    private String message_id;

    @Column(name = "chat_id")
    private String chat_id;

    @Column(name = "sender_id")
    private String sender_id;

    @Column(name = "content")
    private String content;

    @Column(name = "sent_at")
    private Timestamp sent_at;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SentTo> deliveries = new HashSet<>();

    // Contructors
    public Message() {

    }

    public Message(String chat_id, String sender_id, Timestamp sent_at, String content) {
        this.chat_id = chat_id;
        this.sender_id = sender_id;
        this.sent_at = sent_at;
        this.content = content;
    }

    // Getters and setters
    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public Timestamp getSent_at() {
        return sent_at;
    }

    public void setSent_at(Timestamp sent_at) {
        this.sent_at = sent_at;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<SentTo> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Set<SentTo> deliveries) {
        this.deliveries = deliveries;
    }
}