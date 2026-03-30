package com.nexus.nexuschat.SQLitedatabase;

import com.nexus.nexuschat.SQLitedatabase.AppConfig;
import com.nexus.nexuschat.SQLitedatabase.model.*;
import com.nexus.nexuschat.SQLitedatabase.service.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

public class DatabasePopulatorMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        ContactService contactService = context.getBean(ContactService.class);
        IdentityService identityService = context.getBean(IdentityService.class);
        GroupChatService groupChatService = context.getBean(GroupChatService.class);
        MessageService messageService = context.getBean(MessageService.class);
        FriendRequestService friendRequestService = context.getBean(FriendRequestService.class);
        ChatMemberService chatMemberService = context.getBean(ChatMemberService.class);
        GroupRequestService groupRequestService = context.getBean(GroupRequestService.class);

        Random random = new Random();

        Identity daniel = identityService.readIdentity();

        // 2️⃣ Create 10 other contacts
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contactService.retrieveContactById(daniel.getUserId())); // DanielSku included
        for (int i = 1; i <= 10; i++) {
            Contact c = contactService.createContact(UUID.randomUUID().toString(), "user" + i);
            contacts.add(c);
        }

        // 3️⃣ Create 10 friend requests (random pairs)
        Set<String> usedPairs = new HashSet<>();
        int createdFriendRequests = 0;
        while (createdFriendRequests < 10) {
            Contact sender = contacts.get(random.nextInt(contacts.size()));
            Contact receiver = contacts.get(random.nextInt(contacts.size()));
            if (sender.getContactId().equals(receiver.getContactId())) continue;

            String pairKey = sender.getContactId() + "_" + receiver.getContactId();
            String reverseKey = receiver.getContactId() + "_" + sender.getContactId();
            if (usedPairs.contains(pairKey) || usedPairs.contains(reverseKey)) continue;

            FriendRequest fr = new FriendRequest();
            fr.setSenderId(sender.getContactId());
            fr.setReceiverId(receiver.getContactId());
            fr.setUsername(receiver.getUsername());
            fr.setStat("PENDING");

            try {
                friendRequestService.createFriendRequest(fr);
                usedPairs.add(pairKey);
                createdFriendRequests++;
            } catch (RuntimeException e) {
                if (e.getMessage().contains("already exists")) continue;
                else throw e;
            }
        }

        // 4️⃣ Create 3 group chats with DanielSku as member & admin
        List<GroupChat> groupChats = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Set<Contact> members = new HashSet<>();
            Collections.shuffle(contacts);
            members.addAll(contacts.subList(0, 3)); // pick 3 random members
            members.add(contactService.retrieveContactById(daniel.getUserId())); // ensure DanielSku is included

            GroupChat chat = groupChatService.createGroupChat(members, daniel.getUserId(), "GroupChat" + i);
            groupChats.add(chat);
        }

        // 5️⃣ Populate 400 messages from DanielSku
        // 5️⃣ Populate 400 messages with some from DanielSku
        for (int i = 0; i < 400; i++) {
            GroupChat chat = groupChats.get(random.nextInt(groupChats.size()));
            List<ChatMember> members = chatMemberService.retrieveMembersByChat(chat.getChatId());

            ChatMember sender;
            // 20% chance DanielSku sends this message
            if (random.nextInt(100) < 20) {
                sender = members.stream()
                        .filter(m -> m.getUserId().equals(daniel.getUserId()))
                        .findFirst()
                        .orElse(members.get(random.nextInt(members.size()))); // fallback
            } else {
                sender = members.get(random.nextInt(members.size()));
            }

            Message msg = new Message();
            msg.setChatId(chat.getChatId());
            msg.setUserId(sender.getUserId());
            msg.setContent("Message " + i + " from " + sender.getUserId());
            messageService.storeMessage(msg);
        }

        // 6️⃣ Create 10 group chat requests (to random contacts not already in chat)
        int createdGroupRequests = 0;
        while (createdGroupRequests < 10) {
            Contact receiver = contacts.get(random.nextInt(contacts.size()));
            GroupChat chat = groupChats.get(random.nextInt(groupChats.size()));

            if (chat.getCreatedBy().equals(receiver.getContactId())) continue; // skip if admin
            List<ChatMember> members = chatMemberService.retrieveMembersByChat(chat.getChatId());
            boolean receiverInChat = members.stream()
                    .anyMatch(cm -> cm.getUserId().equals(receiver.getContactId()));
            if (receiverInChat) continue;

            GroupRequest gr = new GroupRequest();
            gr.setRequestId(UUID.randomUUID().toString());
            gr.setSenderId(daniel.getUserId()); // DanielSku sends request
            gr.setReceiverId(receiver.getContactId());
            gr.setChatName(chat.getGroupName());
            gr.setStat("PENDING");
            gr.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            try {
                groupRequestService.createGroupRequest(gr);
                createdGroupRequests++;
            } catch (RuntimeException e) {
                if (e.getMessage().contains("already exists")) continue;
                else throw e;
            }
        }

        System.out.println("Database population complete with DanielSku included in every group chat!");
        context.close();
    }
}