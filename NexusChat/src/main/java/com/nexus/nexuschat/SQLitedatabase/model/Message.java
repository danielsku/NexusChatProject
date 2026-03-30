package com.nexus.nexuschat.SQLitedatabase.model;

import java.sql.Timestamp;

public class Message {

    private String mId;
    private String chatId;
    private String userId;
    private String content;
    private Timestamp sentAt;

    public Message() {
    }

    public Message(String mId, String chatId, String userId, String content, Timestamp sentAt) {
        this.mId = mId;
        this.chatId = chatId;
        this.userId = userId;
        this.content = content;
        this.sentAt = sentAt;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "mId='" + mId + '\'' +
                ", chatId='" + chatId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }
}
