package com.nexus.nexuschat.pojo;

public class GroupMember {
    private String requestId; // Used interchangeably with chatId
    private String receiverId;
    private String username;
    private String stat;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public GroupMember() {
    }

    public GroupMember(String requestId, String receiverId, String username, String stat) {
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.username = username;
        this.stat = stat;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "requestId='" + requestId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", username='" + username + '\'' +
                ", stat='" + stat + '\'' +
                '}';
    }
}

