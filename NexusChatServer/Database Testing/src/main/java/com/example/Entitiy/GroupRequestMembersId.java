package com.example.Entitiy;

import java.io.Serializable;
import java.util.Objects;

public class GroupRequestMembersId implements Serializable {

    private String request_id;
    private String receiver_id;

    public GroupRequestMembersId() {}

    public GroupRequestMembersId(String request_id, String receiver_id) {
        this.request_id = request_id;
        this.receiver_id = receiver_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupRequestMembersId)) return false;
        GroupRequestMembersId that = (GroupRequestMembersId) o;
        return Objects.equals(request_id, that.request_id) &&
                Objects.equals(receiver_id, that.receiver_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request_id, receiver_id);
    }

    // getters/setters (optional but recommended)

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }
}