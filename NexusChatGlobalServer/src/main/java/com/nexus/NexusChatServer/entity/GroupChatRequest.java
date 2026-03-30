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
    private String request_id;

    @Column(name = "sender_id")
    private String sender_id;

    @Column(name = "chat_name")
    private String chat_name;

    @Column(name = "created_at")
    private Timestamp created_at;

    public GroupChatRequest() {
    }

    public GroupChatRequest(String request_id,
                            String sender_id,
                            String chat_name,
                            Timestamp created_at) {
        this.request_id = request_id;
        this.sender_id = sender_id;
        this.chat_name = chat_name;
        this.created_at = created_at;
    }

    public String getId() {
        return request_id;
    }

    public void setId(String id) {
        this.request_id = id;
    }

    public String getSenderId() {
        return sender_id;
    }

    public void setSenderId(String sender_id) {
        this.sender_id = sender_id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }
}
