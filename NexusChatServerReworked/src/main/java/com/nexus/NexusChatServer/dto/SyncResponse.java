package com.nexus.NexusChatServer.dto;


import com.nexus.NexusChatServer.entity.ChatFriend;
import com.nexus.NexusChatServer.entity.FriendRequest;
import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.entity.Message;

import java.util.List;

// 🔹 Response DTO
public class SyncResponse {
    private List<Message> messages;
    private List<FriendRequest> friendRequests;
    private List<GroupChatRequest> groupRequests;
    private List<ChatFriend> chatFriends;

    public SyncResponse() {
    }

    public SyncResponse(List<Message> messages,
                        List<FriendRequest> friendRequests,
                        List<GroupChatRequest> groupRequests,
                        List<ChatFriend> chatFriends) {
        this.messages = messages != null ? messages : List.of();
        this.friendRequests = friendRequests != null ? friendRequests : List.of();
        this.groupRequests = groupRequests != null ? groupRequests : List.of();
        this.chatFriends = chatFriends != null ? chatFriends : List.of();
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

    public List<ChatFriend> getChatFriends() {
        return chatFriends;
    }
}
