package com.nexus.NexusChatServer.config;

import com.nexus.NexusChatServer.artifacts.FriendRequest;
import com.nexus.NexusChatServer.controller.websocket.WebSocketSessionManager;
import com.nexus.NexusChatServer.dto.GroupMember;
import com.nexus.NexusChatServer.dto.GroupRequestPayload;
import com.nexus.NexusChatServer.dto.Message;

import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.entity.GroupRequestMembers;
import com.nexus.NexusChatServer.entity.MessageDelivery;
import com.nexus.NexusChatServer.entity.SentTo;
import com.nexus.NexusChatServer.service.FriendRequest.FriendRequestService;
import com.nexus.NexusChatServer.service.GroupChatRequest.GroupChatRequestService;
import com.nexus.NexusChatServer.service.GroupRequestMembers.GroupRequestMembersService;
import com.nexus.NexusChatServer.service.Message.MessageService;
import com.nexus.NexusChatServer.service.MessageDelivery.MessageDeliveryService;
import com.nexus.NexusChatServer.service.SentTo.SentToService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class WebsocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;
    private final FriendRequestService friendRequestService;
    private final GroupChatRequestService groupRequestService;
    private final GroupRequestMembersService groupRequestMembersService;
    private final MessageService messageService;
    private final MessageDeliveryService messageDeliveryService;
    private final SentToService sendToService;

    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate,
                               WebSocketSessionManager sessionManager,
                               FriendRequestService friendRequestService,
                               GroupChatRequestService groupRequestService,
                               GroupRequestMembersService groupRequestMembersService, MessageService messageService, MessageDeliveryService messageDeliveryService, SentToService sendToService) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
        this.friendRequestService = friendRequestService;
        this.groupRequestService = groupRequestService;
        this.groupRequestMembersService = groupRequestMembersService;
        this.messageService = messageService;
        this.messageDeliveryService = messageDeliveryService;
        this.sendToService = sendToService;
    }

    // In all cases, server gets information and handles by sending to the proper receiver in two cases
    // 1. If online --> Send it to the receiving clients via WebSocket
    // 2. If offline --> Store it in the MySQL relay server until client calls sync function once online

    @MessageMapping("/messages")
    public void handleMessage(Message message) {
        System.out.println("Received message from user: " + message.getUser() + ": " + message.getMessage());

        List<String> chatUsers = message.getReceiverList();

        List<String> offlineUsers = new ArrayList<>();
        for(String chatMemberId : chatUsers){

            // Sort the users out if offline
            if(!sessionManager.getActiveUsernames().contains(chatMemberId)){
                offlineUsers.add(chatMemberId);
            }
        }

        // Send once for online users via WebSocket
        messagingTemplate.convertAndSend("/topic/messages/" + message.getChat_id(), message); // (Server) Broadcast user message to all the users in group chat
        System.out.println("Sent message to /topic/messages/" + message.getChat_id() +
                ": " + message.getUser() + ": " + message.getMessage());

        // If there are any offline users, store in the db so they get it via Sync later
        if(!offlineUsers.isEmpty()){
            // Create entry in Message
            com.nexus.NexusChatServer.entity.Message m = messageService.createMessage(new com.nexus.NexusChatServer.entity.Message(
                    UUID.randomUUID().toString(),
                    message.getChat_id(),   // Chat id
                    message.getUser_id(),   // Sender id
                    message.getSent_at(),   // When it was sent
                    message.getMessage()    // Content
            ));
            for(String u : offlineUsers){
                // Create an entry in MessageDelivery for every offline user
                // TODO: MessageDelivery is not needed, remove in later versions
                MessageDelivery md = messageDeliveryService.getById(u);
                if (md == null) {
                    md = messageDeliveryService.create(new MessageDelivery(u));
                }

                // Create a ManyToMany entry for every Message-MessageDelivery
                sendToService.create(new SentTo(m, md));
            }

        }
    }

    @MessageMapping("/friend-requests")
    public void handleFriendRequest(FriendRequest friendRequest){
        System.out.println("Received friend request from user: " + friendRequest.getUsername() + ": " + friendRequest.getRequestId());
        // Check if user is offline or offline
        if(sessionManager.getActiveUsernames().contains(friendRequest.getReceiverId())) {
            messagingTemplate.convertAndSend("/topic/friend-requests/" + friendRequest.getReceiverId(), friendRequest);
            System.out.println("Sent message to /topic/friend-requests/" + friendRequest.getReceiverId() + ": " +
                    friendRequest.getUsername() + ": " + friendRequest.getRequestId());
        }

        com.nexus.NexusChatServer.entity.FriendRequest fr = new com.nexus.NexusChatServer.entity.FriendRequest(
                friendRequest.getRequestId(),
                friendRequest.getSenderId(),
                friendRequest.getReceiverId(),
                friendRequest.getUsername(),
                friendRequest.getStat(),
                friendRequest.getCreatedAt()
        );
        System.out.println("Received friend request " + fr);

        friendRequestService.createFriendRequest(fr);
    }

    @MessageMapping("/group-requests")
    public void handleGroupRequest(GroupRequestPayload groupRequestPayload){
        System.out.println("Received group request from user " + groupRequestPayload.getSenderId() + " for chat " + groupRequestPayload.getChatName());

        // Send the requests to all online users
        for(GroupMember groupMember : groupRequestPayload.getGroupMembers()){
            if(groupMember.getReceiverId().equals(groupRequestPayload.getSenderId())){
                continue;
            }
            if(sessionManager.getActiveUsernames().contains(groupMember.getReceiverId())) {
                messagingTemplate.convertAndSend("/topic/group-requests/" + groupMember.getReceiverId(), groupRequestPayload);
                System.out.println("Send message to /topic/group-requests/" + groupMember.getReceiverId() + ": " + groupMember.getUsername());
            } else {
                System.out.println(groupMember.getReceiverId() + " is offline, will receive in sync");
            }
        }

        // Save the request in the MySQL server, offline users will retrieve this information during syncing
        GroupChatRequest theGroupRequest = groupRequestService.createRequest(new GroupChatRequest(
                groupRequestPayload.getChatId(),
                groupRequestPayload.getSenderId(),
                groupRequestPayload.getChatName(),
                new Timestamp(System.currentTimeMillis())
        ));

        // Save all group request members in the GroupMember table
        for(GroupMember gm : groupRequestPayload.getGroupMembers()){
            // The status user is "Pending" unless it's the sender (automatically "ACCEPTED")
            String status = gm.getReceiverId().equals(groupRequestPayload.getSenderId()) ? "ACCEPTED" : "PENDING";
            groupRequestMembersService.createMember(new GroupRequestMembers(
                    theGroupRequest.getRequestId(),    // RequestId is the same as ChatId
                    gm.getReceiverId(),                 // Id of the group member
                    gm.getUsername(),                   // Username of the group member
                    status                              // Status of the user
            ));
        }

        System.out.println("Group request " + groupRequestPayload.getChatId() + " and its members is saved to the database");
    }

    // For when a user connects
    @MessageMapping("/connect")
    public void connectUser(String username) {
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " connected");
    }

    // For when a user disconnects
    @MessageMapping("/disconnect")
    public void disconnectUser(String username) {
        sessionManager.removeUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " disconnected");
    }

    // For when a client requests all users online
    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.broadcastActiveUsernames();
        System.out.println("Requesting Users");
    }
}
