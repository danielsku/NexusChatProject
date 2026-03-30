package com.nexus.NexusChatServer.artifacts;

import java.sql.Timestamp;

public class FriendRequest {

    private String requestId;
    private String senderId;
    private String receiverId;
    private String username;
    private String stat;
    private Timestamp createdAt;

    public FriendRequest() {
    }

    public FriendRequest(String requestId, String senderId, String receiverId, String username, String stat, Timestamp createdAt) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.username = username;
        this.stat = stat;
        this.createdAt = createdAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
