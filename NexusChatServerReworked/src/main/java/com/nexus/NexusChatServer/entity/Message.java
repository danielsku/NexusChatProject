package com.nexus.NexusChatServer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @Column(name="message_id")
    private String mId;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "sender_id")
    private String userId;

    @Column(name = "content")
    private String content;

    @Column(name = "sent_at")
    private Timestamp sentAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SentTo> deliveries = new ArrayList<>();

    // Constructors
    public Message() {

    }

    public Message(String mId, String chatId, String userId, Timestamp sentAt, String content) {
        this.mId = mId;
        this.chatId = chatId;
        this.userId = userId;
        this.sentAt = sentAt;
        this.content = content;
    }

    // Getters and setters
    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<SentTo> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<SentTo> deliveries) {
        this.deliveries = deliveries;
    }
}