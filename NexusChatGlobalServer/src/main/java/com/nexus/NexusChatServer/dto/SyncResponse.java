package com.nexus.NexusChatServer.dto;


import com.nexus.NexusChatServer.entity.FriendRequest;
import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.entity.Message;

import java.util.List;

// 🔹 Response DTO
public class SyncResponse {
    private List<Message> messages;
    private List<FriendRequest> friendRequests;
    private List<GroupChatRequest> groupRequests;

    public SyncResponse() {
    }

    public SyncResponse(List<Message> messages, List<FriendRequest> friendRequests,
                        List<GroupChatRequest> groupRequests) {
        this.messages = messages != null ? messages : List.of();
        this.friendRequests = friendRequests != null ? friendRequests : List.of();
        this.groupRequests = groupRequests != null ? groupRequests : List.of();
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
