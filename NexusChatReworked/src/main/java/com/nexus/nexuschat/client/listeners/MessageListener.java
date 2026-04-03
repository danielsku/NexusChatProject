package com.nexus.nexuschat.client.listeners;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.Message;

import java.util.ArrayList;

public interface MessageListener {
    void onMessageReceive(Message message);
    void onFriendRequestReceive(FriendRequest friendRequest);
    void onGroupRequestReceive(GroupRequestPayload groupRequestPayload);
    void onActiveUsersUpdated(ArrayList<String> users);
    void onGroupCreated(GroupChat groupChat);
}
