package com.nexus.nexuschat.SQLitedatabase.model;

public class Identity {

    private String userId;

    public Identity() {
    }

    public Identity(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Identity{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
