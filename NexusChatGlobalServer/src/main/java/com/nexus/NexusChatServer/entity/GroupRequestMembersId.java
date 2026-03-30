package com.nexus.NexusChatServer.entity;

import java.io.Serializable;
import java.util.Objects;

public class GroupRequestMembersId implements Serializable {

    private String requestId;
    private String receiverId;

    public GroupRequestMembersId() {}

    public GroupRequestMembersId(String requestId, String receiverId) {
        this.requestId = requestId;
        this.receiverId = receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupRequestMembersId)) return false;
        GroupRequestMembersId that = (GroupRequestMembersId) o;
        return Objects.equals(requestId, that.requestId) &&
                Objects.equals(receiverId, that.receiverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, receiverId);
    }

    // getters/setters (optional but recommended)

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
}