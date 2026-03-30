package com.nexus.nexuschat.pojo;

import java.util.Arrays;
import java.util.List;

public class GroupRequestPayload {

    private String senderId;
    private String chatId;
    private String chatName;
    private List<GroupMember> groupMembers;

    public GroupRequestPayload() {
    }

    public GroupRequestPayload(String senderId, String chatId, String chatName, List<GroupMember> groupMembers) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.chatName = chatName;
        this.groupMembers = groupMembers;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @Override
    public String toString() {
        return "GroupRequestPayload{" +
                "senderId='" + senderId + '\'' +
                ", chatId='" + chatId + '\'' +
                ", chatName='" + chatName + '\'' +
                ", groupMembers=" + groupMembers +
                '}';
    }
}
