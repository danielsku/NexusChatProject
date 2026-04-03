package com.example.Entitiy;

import java.sql.Timestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "friend_request")
public class FriendRequest {

    @Id
    @Column(name = "request_id")
    private String id;

    @Column(name = "sender_id")
    private String sender_id;

    @Column(name = "receiver_id")
    private String receiver_id;

    @Column(name = "username")
    private String username;

    @Column(name = "stat")
    private String stat;

    @Column(name = "created_at")
    private Timestamp created_at;


    // Contructors
    public FriendRequest() {

    }

    public FriendRequest(String id,
                         String sender_id,
                         String receiver_id,
                         String username,
                         String stat,
                         Timestamp created_at) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.username = username;
        this.stat = stat;
        this.created_at = created_at;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String message_id) {
        this.id = message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return stat;
    }

    public void setStatus(String stat) {
        this.stat = stat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}