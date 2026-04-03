package com.nexus.nexuschat.websocketconfig;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.WebSocketSessionManager;
import com.nexus.nexuschat.pojo.GroupMember;
import com.nexus.nexuschat.pojo.GroupRequestPayload;
import com.nexus.nexuschat.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
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
        messagingTemplate.convertAndSend("/topic/friend-requests/" + friendRequest.getReceiverId(), friendRequest);
        System.out.println("Sent message to /topic/friend-requests/" + friendRequest.getReceiverId() + ": " +
                friendRequest.getUsername() + ": " + friendRequest.getRequestId());
    }

    @MessageMapping("/group-requests")
    public void handleGroupRequest(GroupRequestPayload groupRequestPayload){
        System.out.println("Received group request from user ");
        for(GroupMember groupMember : groupRequestPayload.getGroupMembers()){
            if(groupMember.getReceiverId().equals(groupRequestPayload.getSenderId())){
                continue;
            }
            messagingTemplate.convertAndSend("/topic/group-requests/" + groupMember.getReceiverId(), groupRequestPayload);
            System.out.println("Send message to /topic/group-requests/" + groupMember.getReceiverId() + ": " + groupMember.getUsername());
        }
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
