package com.nexus.NexusChatServer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "friend_request")
public class FriendRequest {

    @Id
    @Column(name = "request_id")
    private String requestId;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "username")
    private String username;

    @Column(name = "stat")
    private String stat;

    @Column(name = "created_at")
    private Timestamp createdAt;


    // Contructors
    public FriendRequest() {

    }

    public FriendRequest(String requestId,
                         String senderId,
                         String receiverId,
                         String username,
                         String stat,
                         Timestamp createdAt) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.username = username;
        this.stat = stat;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String message_id) {
        this.requestId = message_id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "requestId='" + requestId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", username='" + username + '\'' +
                ", stat='" + stat + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}