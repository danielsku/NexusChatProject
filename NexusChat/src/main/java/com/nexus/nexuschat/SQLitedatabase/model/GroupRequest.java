package com.nexus.nexuschat.SQLitedatabase.model;

import java.sql.Timestamp;

public class GroupRequest {

    private String requestId;
    private String senderId;
    private String receiverId;
    private String chatName;
    private String stat;
    private Timestamp createdAt;

    public GroupRequest() {
    }

    public GroupRequest(String requestId, String senderId, String receiverId, String chatName, String stat, Timestamp createdAt) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatName = chatName;
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

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "requestId='" + requestId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", chat_name='" + chatName + '\'' +
                ", stat='" + stat + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
