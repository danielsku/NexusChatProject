package com.nexus.nexuschat.SQLitedatabase.dao.chatmember;


import com.nexus.nexuschat.SQLitedatabase.model.ChatMember;

import java.util.List;

public interface ChatMemberDAO {

    // Create
    void saveChatMember(ChatMember chatMember);

    // Read
    List<ChatMember> findAllMembersByChat(String chatId);
    List<ChatMember> findAllChatsByUser(String userId);
    boolean findChatMember(String chatId, String userId);

    // Delete
    ChatMember removeChatMember(ChatMember chatMember);

    // delete all members from a chat
    void removeAllMembersFromChat(String chatId);

}


