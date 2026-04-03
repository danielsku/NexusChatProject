package com.nexus.NexusChatServer.controller.rest;

import com.nexus.NexusChatServer.controller.websocket.WebSocketSessionManager;
import com.nexus.NexusChatServer.dto.ChatFriendPayload;
import com.nexus.NexusChatServer.dto.GroupMember;
import com.nexus.NexusChatServer.entity.ChatFriend;
import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.entity.GroupRequestMembers;
import com.nexus.NexusChatServer.entity.GroupRequestMembersId;
import com.nexus.NexusChatServer.service.ChatFriend.ChatFriendService;
import com.nexus.NexusChatServer.service.GroupChatRequest.GroupChatRequestService;
import com.nexus.NexusChatServer.service.GroupRequestMembers.GroupRequestMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

// TODO: For every Controller, use the DTO/POJO model instead of the JPA entity

@RestController
@RequestMapping("/api")
public class GroupRequestMembersController {

    private final GroupRequestMembersService groupRequestMembersService;
    private final GroupChatRequestService groupChatRequestService;
    private final ChatFriendService chatFriendService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public GroupRequestMembersController(GroupRequestMembersService groupRequestMembersService,
                                         GroupChatRequestService groupChatRequestService,
                                         ChatFriendService chatFriendService,
                                         SimpMessagingTemplate messagingTemplate,
                                         WebSocketSessionManager sessionManager) {
        this.groupRequestMembersService = groupRequestMembersService;
        this.groupChatRequestService = groupChatRequestService;
        this.chatFriendService = chatFriendService;
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    // PUT /group-request-members/{requestId}/{receiverId} → accept/decline
    @PutMapping("/group-request-members/{requestId}/{receiverId}")
    public GroupRequestMembers updateRequestMemberStatus(
            @PathVariable String requestId,
            @PathVariable String receiverId,
            @RequestBody GroupMember groupMember) {

        GroupRequestMembersId memberId = new GroupRequestMembersId(requestId, receiverId);
        GroupRequestMembers member = groupRequestMembersService.getMembersByRequestId(requestId)
                .stream()
                .filter(m -> m.getReceiverId().equals(receiverId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group request member not found"));

        member.setStat(groupMember.getStat()); // "ACCEPTED" or "DECLINED"
        groupRequestMembersService.updateMember(member);

        // if user accepted it, they exchange contact with all currently accepted users
        if(member.getStat().equals("ACCEPTED")) {
            // Get all accepted members
            List<GroupRequestMembers> allAcceptedMembers =
                    groupRequestMembersService.getMembersByRequestId(requestId)
                            .stream()
                            .filter(gm -> gm.getStat().equals("ACCEPTED"))
                            .toList();

            System.out.println("Accepted members : " + allAcceptedMembers);
            System.out.println("Active users: " + sessionManager.getActiveUsernames());
            // Send payload for accepted members to add chatMember to their local db, and an automatic friend acceptance, if offline, add to sync
            for (GroupRequestMembers gm : allAcceptedMembers) {
                System.out.println("Trying to send to: " + gm.getReceiverId());
                if(sessionManager.getActiveUsernames().contains(gm.getReceiverId())){
                    // send payload
                    messagingTemplate.convertAndSend(
                            "/topic/group-created/" + gm.getReceiverId(),
                            new ChatFriendPayload(
                                    UUID.randomUUID().toString(),  // new ChatFriendPayload id
                                    requestId,                     // Group chatId
                                    receiverId,                    // The sender of the ChatFriendPayload
                                    gm.getReceiverId(),            // Who we are sending it to
                                    gm.getUsername(),              // The sender's username
                                    member.getUsername(),          // receiver's username
                                    "ACCEPTED",                    // Status of ChatFriendPayload
                                    new Timestamp(System.currentTimeMillis()) // When the payload was created
                            )
                    );
                    System.out.println("\nSent to /topic/group-created/" + gm.getReceiverId());
                } else {
                    chatFriendService.createChatFriend(new ChatFriend(
                            UUID.randomUUID().toString(),  // new ChatFriendPayload id
                            requestId,                     // Group chatId
                            receiverId,                    // The sender of the ChatFriendPayload
                            gm.getReceiverId(),            // Who we are sending it to
                            gm.getUsername(),              // The sender's username
                            member.getUsername(),          // receiver's username
                            "ACCEPTED",                    // Status of ChatFriendPayload
                            new Timestamp(System.currentTimeMillis()) // When the payload was created
                    ));
                }
            }

        }

        // Check if all groupChat members ACCEPTED/DECLINED,
        boolean flagForDeletion = true;
        for(GroupRequestMembers gm : groupRequestMembersService.getMembersByRequestId(requestId)){
            if(gm.getStat().equals("PENDING")){
                flagForDeletion = false;
                break;
            }
        }

        //  Remove all group chat members and group Request from MySQL database
        if(flagForDeletion){
            GroupChatRequest request = groupChatRequestService.getGroupChatRequestById(requestId);
            // Delete all GroupRequestMembers first
            for(GroupRequestMembers gm : groupRequestMembersService.getRequestIdByMember(requestId)){
                groupRequestMembersService.deleteMember(gm);
            }
            if (request != null) {
                groupChatRequestService.deleteRequest(request);
            }
        }

        return groupRequestMembersService.updateMember(member);
    }

    public static class UpdateStatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // GET /group-request-members/{requestId} → get all members for a request
    @GetMapping("/group-request-members/{requestId}")
    public List<GroupMember> getAllMembersByRequestId(@PathVariable String requestId) {

        return groupRequestMembersService.getMembersByRequestId(requestId)
                .stream()
                .map(m -> new GroupMember(
                        m.getRequestId(),
                        m.getReceiverId(),
                        m.getUsername(),
                        m.getStat()
                ))
                .toList();
    }
}