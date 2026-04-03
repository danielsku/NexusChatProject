package com.nexus.nexuschat.SQLitedatabase.service;


import com.nexus.nexuschat.SQLitedatabase.dao.chatmember.ChatMemberDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.groupchat.GroupChatDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.message.MessageDAO;
import com.nexus.nexuschat.SQLitedatabase.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private MessageDAO messageDAO;
    private ChatMemberDAO chatMemberDAO;
    private GroupChatDAO groupChatDAO;

    @Autowired
    public MessageService(MessageDAO messageDAO, ChatMemberDAO chatMemberDAO, GroupChatDAO groupChatDAO) {
        this.messageDAO = messageDAO;
        this.chatMemberDAO = chatMemberDAO;
        this.groupChatDAO = groupChatDAO;
    }

    public Message storeMessage(Message message){
        if (message.getmId() == null) {
            message.setmId(UUID.randomUUID().toString());
        }

        if(message.getmId() == null || message.getChatId() == null
        || message.getUserId() == null){
            throw new RuntimeException("Invalid message");
        }

        if(groupChatDAO.findGroupChatById(message.getChatId()) == null){
            throw new RuntimeException("Group chat " + message.getChatId() + " does not exist");
        }

        if(!chatMemberDAO.findChatMember(message.getChatId(), message.getUserId())){
            throw new RuntimeException("User "  + message.getUserId() + " is not in group chat" + message.getChatId());
        }

        message.setSentAt(new Timestamp(System.currentTimeMillis()));
        messageDAO.saveMessage(message);

        return message;
    }

    public List<Message> loadLatestMessages(String chatId, int limit, Timestamp latestTime, String mId){
        if(groupChatDAO.findGroupChatById(chatId) == null){
            throw new RuntimeException("Group chat" + chatId + " does not exist");
        }
        return messageDAO.findMessageByLatest(chatId, limit, latestTime, mId);
    }
}
