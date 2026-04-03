package com.nexus.NexusChatServer.dto;

import java.sql.Timestamp;
import java.util.List;

public class Message {
    private String user;
    private String message;
    private String user_id;
    private String chat_id;
    private Timestamp sent_at;
    private List<String> receiverList;

    public Message() {
    }

    public Message(String user,
                   String message,
                   String user_id,
                   String chat_id,
                   Timestamp sent_at,
                   List<String> receiverList) {
        this.user = user;
        this.message = message;
        this.user_id = user_id;
        this.chat_id = chat_id;
        this.sent_at = sent_at;
        this.receiverList = receiverList;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public Timestamp getSent_at() {
        return sent_at;
    }

    public List<String> getReceiverList() {
        return receiverList;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", user_id='" + user_id + '\'' +
                ", chat_id='" + chat_id + '\'' +
                ", sent_at=" + sent_at +
                ", receiverList=" + receiverList +
                '}';
    }
}
