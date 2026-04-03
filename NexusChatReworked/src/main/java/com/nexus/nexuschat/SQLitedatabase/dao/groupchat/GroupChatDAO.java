package com.nexus.nexuschat.SQLitedatabase.dao.groupchat;

import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;

import java.util.List;

public interface GroupChatDAO {

    // Create
    void saveGroupChat(GroupChat groupChat);

    //Read
    GroupChat findGroupChatById(String chatId);
    List<GroupChat> findAllGroupChats();

    // Update -- Non needed

    // Delete
    GroupChat deleteGroupChat(GroupChat groupchat);
}
