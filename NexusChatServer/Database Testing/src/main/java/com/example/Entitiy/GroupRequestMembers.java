package com.example.Entitiy;

import jakarta.persistence.*;

@Entity
@Table(name = "group_request_member")
@IdClass(GroupRequestMembersId.class)
public class GroupRequestMembers {
    @Id
    @Column(name="request_id")
    private String request_id;

    @Id
    @Column(name="receiver_id")
    private String receiver_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "request_id", insertable = false, updatable = false)
    private GroupChatRequest request;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "stat")
    private String stat;

    public GroupRequestMembers(){}

    public GroupRequestMembers(String request_id, String receiver_id, String username, String stat){
        this.request_id = request_id;
        this.receiver_id = receiver_id;
        this.username = username;
        this.stat = stat;
    }

    public String getRequestId() {
        return request_id;
    }

    public void setRequestId(String requestId) {
        this.request_id = requestId;
    }

    public String getReceiverId() {
        return receiver_id;
    }

    public void setReceiverId(String receiverId) {
        this.receiver_id = receiverId;
    }

    public GroupChatRequest getRequest() {
        return request;
    }

    public void setRequest(GroupChatRequest request) {
        this.request = request;
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
