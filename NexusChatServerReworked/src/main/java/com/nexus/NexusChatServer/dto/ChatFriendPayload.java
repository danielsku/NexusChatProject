package com.nexus.NexusChatServer.dto;

import java.sql.Timestamp;

public class ChatFriendPayload {
    private String requestId;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String senderName;
    private String receiverName;
    private String stat;
    private Timestamp createdAt;

    public ChatFriendPayload() {
    }

    public ChatFriendPayload(String requestId,
                             String chatId,
                             String senderId,
                             String receiverId,
                             String senderName,
                             String receiverName,
                             String stat,
                             Timestamp createdAt) {
        this.requestId = requestId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.stat = stat;
        this.createdAt = createdAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    @Override
    public String toString() {
        return "ChatFriendPayload{" +
                "requestId='" + requestId + '\'' +
                ", chatId='" + chatId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", stat='" + stat + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
