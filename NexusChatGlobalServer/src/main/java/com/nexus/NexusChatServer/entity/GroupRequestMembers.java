package com.nexus.NexusChatServer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "group_request_member")
@IdClass(GroupRequestMembersId.class)
public class GroupRequestMembers {
    @Id
    @Column(name="request_id")
    private String requestId;

    @Id
    @Column(name="receiver_id")
    private String receiverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "request_id", insertable = false, updatable = false)
    private GroupChatRequest request;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "stat")
    private String stat;

    public GroupRequestMembers(){}

    public GroupRequestMembers(String requestId, String receiverId, String username, String stat){
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.username = username;
        this.stat = stat;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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
