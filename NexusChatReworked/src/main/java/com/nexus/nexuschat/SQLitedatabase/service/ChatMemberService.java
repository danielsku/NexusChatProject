package com.nexus.nexuschat.SQLitedatabase.service;


import com.nexus.nexuschat.SQLitedatabase.dao.chatmember.ChatMemberDAO;
import com.nexus.nexuschat.SQLitedatabase.model.ChatMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.List;

@Service
public class ChatMemberService {

    private ChatMemberDAO chatMemberDAO;

    @Autowired
    public ChatMemberService(ChatMemberDAO chatMemberDAO) {
        this.chatMemberDAO = chatMemberDAO;
    }

    public void addMemberToChat(String chatId, String userId) {
        if (chatMemberDAO.findChatMember(chatId, userId)) {
            System.out.println("User is already in chat");
            return;
//            throw new RuntimeException("User already in chat");
        }

        chatMemberDAO.saveChatMember(new ChatMember(chatId, userId));
    }

    public List<ChatMember> retrieveMembersByChat(String chatId) {
        return chatMemberDAO.findAllMembersByChat(chatId);
    }

    public List<ChatMember> retrieveChatsByUser(String userId) {
        return chatMemberDAO.findAllChatsByUser(userId);
    }

    public boolean isUserInChat(String chatId, String userId) {
        return chatMemberDAO.findChatMember(chatId, userId);
    }

    public ChatMember removeMemberFromChat(String chatId, String userId) {
        return chatMemberDAO.removeChatMember(new ChatMember(chatId, userId));
    }

    public void removeAllMembersFromChat(String chatId) {
        chatMemberDAO.removeAllMembersFromChat(chatId);
    }

}
