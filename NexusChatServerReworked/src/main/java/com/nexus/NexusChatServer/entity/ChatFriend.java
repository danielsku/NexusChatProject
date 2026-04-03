package com.nexus.NexusChatServer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name="chat_friend")
public class ChatFriend {

    @Id
    @Column(name="request_id")
    private String requestId;

    @Column(name="chat_id")
    private String chatId;

    @Column(name="sender_id")
    private String senderId;

    @Column(name="receiver_id")
    private String receiverId;

    @Column(name="sender_name")
    private String senderName;

    @Column(name="receiver_name")
    private String receiverName;

    @Column(name="stat")
    private String stat;

    @Column(name="created_at")
    private Timestamp createdAt;

    public ChatFriend() {
    }

    public ChatFriend(String requestId,
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
