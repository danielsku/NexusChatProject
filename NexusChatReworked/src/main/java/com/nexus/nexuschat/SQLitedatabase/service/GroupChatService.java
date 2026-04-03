package com.nexus.nexuschat.SQLitedatabase.service;


import com.nexus.nexuschat.SQLitedatabase.dao.chatmember.ChatMemberDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.contact.ContactDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.groupchat.GroupChatDAO;
import com.nexus.nexuschat.SQLitedatabase.model.ChatMember;
import com.nexus.nexuschat.SQLitedatabase.model.Contact;
import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GroupChatService {

    private GroupChatDAO groupChatDAO;
    private ContactDAO contactDAO;
    private ChatMemberDAO chatMemberDAO;

    @Autowired
    public GroupChatService(GroupChatDAO groupChatDAO, ContactDAO contactDAO, ChatMemberDAO chatMemberDAO) {
        this.groupChatDAO = groupChatDAO;
        this.contactDAO = contactDAO;
        this.chatMemberDAO = chatMemberDAO;
    }

    public GroupChat createGroupChat(Set<Contact> chatMembers, String groupAdminId, String groupName){
        if(chatMembers == null || chatMembers.isEmpty()){
            throw new RuntimeException("Can't create empty group chat");
        }

        Contact admin = contactDAO.findContactById(groupAdminId);

        if(admin == null){
            throw new RuntimeException("Group creator does not exist: " + groupAdminId);
        }
        Set<Contact> allMembers = new HashSet<>(chatMembers);
        allMembers.add(admin);

        String groupId = UUID.randomUUID().toString();

        GroupChat newGroup = new GroupChat(groupId, groupAdminId, new Timestamp(System.currentTimeMillis()), groupName);
        groupChatDAO.saveGroupChat(newGroup);


        for(Contact c : allMembers){
            if(c == null){
                System.out.println("Skipping null member in group creation");
                continue;
            }
            chatMemberDAO.saveChatMember(new ChatMember(groupId, c.getContactId()));
        }

        return newGroup;
    }

    public GroupChat createGroupChat(Set<Contact> chatMembers, String groupAdminId, String groupName, String chatId){
        if(chatMembers == null || chatMembers.isEmpty()){
            throw new RuntimeException("Can't create empty group chat");
        }

        Contact admin = contactDAO.findContactById(groupAdminId);

        if(admin == null){
            throw new RuntimeException("Group creator does not exist: " + groupAdminId);
        }
        Set<Contact> allMembers = new HashSet<>(chatMembers);
        allMembers.add(admin);


        GroupChat newGroup = new GroupChat(chatId, groupAdminId, new Timestamp(System.currentTimeMillis()), groupName);
        groupChatDAO.saveGroupChat(newGroup);

        // Group chat creation is decoupled from adding members to chat

//        for(Contact c : allMembers){
//            if(c == null){
//                System.out.println("Skipping null member in group creation");
//                continue;
//            }
//            chatMemberDAO.saveChatMember(new ChatMember(chatId, c.getContactId()));
//        }

        return newGroup;
    }

    public List<GroupChat> retrieveAllGroupChats(){
        return groupChatDAO.findAllGroupChats();
    }

    public GroupChat retrieveGroupChatById(String chatId){
        GroupChat theChat = groupChatDAO.findGroupChatById(chatId);
        if(theChat == null){
            throw new RuntimeException("Group chat does not exist" + chatId);
        }
        return theChat;
    }

    public GroupChat removeGroupChat(GroupChat groupChat){
        if(groupChat == null
                || groupChatDAO.findGroupChatById(groupChat.getChatId()) == null){
            throw new RuntimeException("Group chat does not exist :" + groupChat);
        }

        chatMemberDAO.removeAllMembersFromChat(groupChat.getChatId());

        return groupChatDAO.deleteGroupChat(groupChat);
    }
}
