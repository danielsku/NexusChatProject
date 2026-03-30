package com.nexus.NexusChatServer.controller.rest;

import com.nexus.NexusChatServer.dto.SyncResponse;
import com.nexus.NexusChatServer.entity.*;
import com.nexus.NexusChatServer.service.FriendRequest.FriendRequestService;
import com.nexus.NexusChatServer.service.GroupChatRequest.GroupChatRequestService;
import com.nexus.NexusChatServer.service.GroupRequestMembers.GroupRequestMembersService;
import com.nexus.NexusChatServer.service.Message.MessageService;
import com.nexus.NexusChatServer.service.SentTo.SentToService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

// TODO: For every Controller, use the DTO/POJO model instead of the JPA entity

@RestController
@RequestMapping("/api")
public class SyncController {

    private final MessageService messageService;
    private final SentToService sentToService;
    private final FriendRequestService friendRequestService;
    private final GroupChatRequestService groupChatRequestService;
    private final GroupRequestMembersService groupRequestMembersService;

    @Autowired
    public SyncController(
            MessageService messageService,
            SentToService sentToService,
            FriendRequestService friendRequestService,
            GroupChatRequestService groupChatRequestService,
            GroupRequestMembersService groupRequestMembersService
    ) {
        this.messageService = messageService;
        this.sentToService = sentToService;
        this.friendRequestService = friendRequestService;
        this.groupChatRequestService = groupChatRequestService;
        this.groupRequestMembersService = groupRequestMembersService;
    }

    @GetMapping("/sync/{userId}")
    public SyncResponse syncUserData(@PathVariable String userId) {

        // 1️⃣ Pending Messages
        List<SentTo> pendingDeliveries = sentToService.getByReceiverId(userId).stream()
                .filter(st -> st.getStat().equals("PENDING"))
                .collect(Collectors.toList());

        List<Message> pendingMessages = pendingDeliveries.stream()
                .map(SentTo::getMessage)
                .collect(Collectors.toList());

        // 2️⃣ Pending Friend Requests
        List<FriendRequest> pendingFriendRequests = friendRequestService.getRequestsByReceiverId(userId).stream()
                .filter(fr -> fr.getStat().equals("WAITING") || (fr.getSenderId().equals(userId) && fr.getStat().equals("ACCEPT")))
                .collect(Collectors.toList());


        // 3️⃣ Pending Group Chat Requests
        // Query GroupRequestMembers to find all members with the matching receiverId
        List<GroupRequestMembers> groupRequestMembers = groupRequestMembersService.getRequestIdByMember(userId).stream()
                .filter(grm -> grm.getStat().equals("PENDING"))  // Only include those that are pending
                .collect(Collectors.toList());

        // Retrieve corresponding GroupChatRequests for each pending GroupRequestMember and filter by "PENDING"
        List<GroupChatRequest> pendingGroupRequests = groupRequestMembers.stream()
                .map(grm -> groupChatRequestService.getGroupChatRequestById(grm.getRequestId())) // Get List<GroupChatRequest> by requestId
                .collect(Collectors.toList());


        return new SyncResponse(pendingMessages, pendingFriendRequests, pendingGroupRequests);
    }
}