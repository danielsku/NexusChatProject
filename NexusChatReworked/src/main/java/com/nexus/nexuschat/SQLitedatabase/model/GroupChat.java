package com.nexus.nexuschat.SQLitedatabase.model;

import java.sql.Timestamp;

public class GroupChat {

    private String chatId;
    private String createdBy;
    private Timestamp createdAt;
    private String groupName;

    public GroupChat() {
    }

    public GroupChat(String chatId, String createdBy, Timestamp createdAt, String groupName) {
        this.chatId = chatId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.groupName = groupName;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "GroupChat{" +
                "chatId='" + chatId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
