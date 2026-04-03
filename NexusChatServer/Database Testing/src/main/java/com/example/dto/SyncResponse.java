package com.example.dto;

import com.example.Entitiy.FriendRequest;
import com.example.Entitiy.GroupChatRequest;
import com.example.Entitiy.GroupRequestMembers;
import com.example.Entitiy.Message;

import java.util.List;

// 🔹 Response DTO
public class SyncResponse {
    private List<Message> messages;
    private List<FriendRequest> friendRequests;
    private List<GroupChatRequest> groupRequests;

    public SyncResponse(List<Message> messages, List<FriendRequest> friendRequests,
                        List<GroupChatRequest> groupRequests) {
        this.messages = messages;
        this.friendRequests = friendRequests;
        this.groupRequests = groupRequests;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public List<GroupChatRequest> getGroupRequests() {
        return groupRequests;
    }

}
