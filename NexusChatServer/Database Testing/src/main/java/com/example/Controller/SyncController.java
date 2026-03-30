package com.example.Controller;

import com.example.Entitiy.*;
import com.example.Service.FriendRequest.FriendRequestService;
import com.example.Service.GroupChatRequest.GroupChatRequestService;
import com.example.Service.GroupRequestMembers.GroupRequestMembersService;
import com.example.Service.Message.MessageService;
import com.example.Service.SentTo.SentToService;
import com.example.dto.SyncResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
                .filter(fr -> fr.getStat().equals("WAITING"))
                .collect(Collectors.toList());

        // 3️⃣ Pending Group Chat Requests
        List<GroupChatRequest> pendingGroupRequests = groupChatRequestService.getRequestsByReceiverId(userId).stream()
                .filter(gr -> gr.getStat().equals("PENDING"))
                .collect(Collectors.toList());

        return new SyncResponse(pendingMessages, pendingFriendRequests, pendingGroupRequests);
    }
}