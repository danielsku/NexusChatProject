package com.nexus.NexusChatServer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "group_request")
public class GroupChatRequest {

    @Id
    @Column(name = "request_id")
    private String requestId;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "chat_name")
    private String chatName;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public GroupChatRequest() {
    }

    public GroupChatRequest(String requestId,
                            String senderId,
                            String chatName,
                            Timestamp createdAt) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.chatName = chatName;
        this.createdAt = createdAt;
    }

    public String getId() {
        return requestId;
    }

    public void setId(String id) {
        this.requestId = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
