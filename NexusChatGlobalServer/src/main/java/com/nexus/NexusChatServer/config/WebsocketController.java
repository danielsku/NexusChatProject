package com.nexus.NexusChatServer.config;

import com.nexus.NexusChatServer.artifacts.FriendRequest;
import com.nexus.NexusChatServer.controller.websocket.WebSocketSessionManager;
import com.nexus.NexusChatServer.dto.GroupMember;
import com.nexus.NexusChatServer.dto.GroupRequestPayload;
import com.nexus.NexusChatServer.dto.Message;

import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.entity.GroupRequestMembers;
import com.nexus.NexusChatServer.service.FriendRequest.FriendRequestService;
import com.nexus.NexusChatServer.service.GroupChatRequest.GroupChatRequestService;
import com.nexus.NexusChatServer.service.GroupRequestMembers.GroupRequestMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;

@Controller
public class WebsocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;
    private FriendRequestService friendRequestService;
    private GroupChatRequestService groupRequestService;
    private GroupRequestMembersService groupRequestMembersService;

    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate,
                               WebSocketSessionManager sessionManager,
                               FriendRequestService friendRequestService,
                               GroupChatRequestService groupRequestService,
                               GroupRequestMembersService groupRequestMembersService) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
        this.friendRequestService = friendRequestService;
        this.groupRequestService = groupRequestService;
        this.groupRequestMembersService = groupRequestMembersService;
    }


    @MessageMapping("/messages")
    public void handleMessage(Message message) {
        System.out.println("Received message from user: " + message.getUser() + ": " + message.getMessage());
        messagingTemplate.convertAndSend("/topic/messages/" + message.getChat_id(), message); // (Server) Broadcast user message to all the users in group chat
        System.out.println("Sent message to /topic/messages/" + message.getChat_id() +
                ": " + message.getUser() + ": " + message.getMessage());
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

        GroupChatRequest theGroupRequest = groupRequestService.createRequest(new GroupChatRequest(
                groupRequestPayload.getChatId(),
                groupRequestPayload.getSenderId(),
                groupRequestPayload.getChatName(),
                new Timestamp(System.currentTimeMillis())
        ));
        for(GroupMember gm : groupRequestPayload.getGroupMembers()){
            String status = gm.getReceiverId().equals(groupRequestPayload.getSenderId()) ? "ACCEPTED" : "PENDING";
            groupRequestMembersService.createMember(new GroupRequestMembers(
                    theGroupRequest.getRequest_id(),
                    gm.getReceiverId(),
                    gm.getUsername(),
                    status
            ));
        }

        System.out.println("Group request " + groupRequestPayload.getChatId() + " and its members is saved to the database");
    }

    @MessageMapping("/connect")
    public void connectUser(String username) {
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " connected");
    }

    @MessageMapping("/disconnect")
    public void disconnectUser(String username) {
        sessionManager.removeUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " disconnected");
    }

    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.broadcastActiveUsernames();
        System.out.println("Requesting Users");
    }
}
