package com.nexus.nexuschat.pojo;

import com.nexus.nexuschat.SQLitedatabase.model.Message;
import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;

import java.util.List;

public class SyncResponse {
    private List<com.nexus.nexuschat.SQLitedatabase.model.Message> messages;
    private List<FriendRequest> friendRequests;
    private List<GroupRequest> groupRequests;

    public SyncResponse() {
    }

    public SyncResponse(List<com.nexus.nexuschat.SQLitedatabase.model.Message> messages,
                        List<FriendRequest> friendRequests,
                        List<GroupRequest> groupRequests) {
        this.messages = messages != null ? messages : List.of();
        this.friendRequests = friendRequests != null ? friendRequests : List.of();
        this.groupRequests = groupRequests != null ? groupRequests : List.of();
    }

    public List<com.nexus.nexuschat.SQLitedatabase.model.Message> getMessages() {
        return messages;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public List<GroupRequest> getGroupRequests() {
        return groupRequests;
    }
}
