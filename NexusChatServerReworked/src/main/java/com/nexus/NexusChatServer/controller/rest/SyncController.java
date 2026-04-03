package com.nexus.NexusChatServer.controller.rest;

import com.nexus.NexusChatServer.dto.SyncResponse;
import com.nexus.NexusChatServer.entity.*;
import com.nexus.NexusChatServer.service.ChatFriend.ChatFriendService;
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
import java.util.Objects;
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
    private final ChatFriendService chatFriendService;

    @Autowired
    public SyncController(
            MessageService messageService,
            SentToService sentToService,
            FriendRequestService friendRequestService,
            GroupChatRequestService groupChatRequestService,
            GroupRequestMembersService groupRequestMembersService, ChatFriendService chatFriendService
    ) {
        this.messageService = messageService;
        this.sentToService = sentToService;
        this.friendRequestService = friendRequestService;
        this.groupChatRequestService = groupChatRequestService;
        this.groupRequestMembersService = groupRequestMembersService;
        this.chatFriendService = chatFriendService;
    }

    @GetMapping("/sync/{userId}")
    public SyncResponse syncUserData(@PathVariable String userId) {

        // 1. Pending Messages

        List<SentTo> pendingDeliveries = sentToService.getByReceiverId(userId);
        System.out.println(pendingDeliveries);
        List<Message> pendingMessages = pendingDeliveries.stream()
                .map(SentTo::getMessage)
                .collect(Collectors.toList());
        System.out.println(pendingMessages);

        // 2. Pending Friend Requests
        List<FriendRequest> pendingFriendRequests = friendRequestService.getRequestsByReceiverId(userId).stream()
                .filter(fr -> fr.getStat().equals("PENDING") || (fr.getSenderId().equals(userId) && fr.getStat().equals("ACCEPT")))
                .collect(Collectors.toList());


        // 3. Pending Group Chat Requests
        // Query GroupRequestMembers to find all members with the matching receiverId
        List<GroupRequestMembers> groupRequestMembers = groupRequestMembersService.getRequestIdByMember(userId).stream()
                .filter(grm -> grm.getStat().equals("PENDING"))  // Only include those that are pending
                .collect(Collectors.toList());

        // Retrieve corresponding GroupChatRequests for each pending GroupRequestMember and filter by "PENDING"
        List<GroupChatRequest> pendingGroupRequests = groupRequestMembers.stream()
                .map(grm -> groupChatRequestService.getGroupChatRequestById(grm.getRequestId())) // Get List<GroupChatRequest> by requestId
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 4. get all pending ChatFriend requests
        List<ChatFriend> pendingChatFriendRequests = chatFriendService.findAllRequestsByReceiver_id(userId);
        System.out.println("\n\n" + pendingMessages + "\n\n" + pendingFriendRequests + "\n\n" + pendingGroupRequests + "\n\n" + pendingChatFriendRequests);
        SyncResponse theResponse = new SyncResponse(pendingMessages, pendingFriendRequests, pendingGroupRequests, pendingChatFriendRequests);

        // For messages
        // Mark them all as read
        for (SentTo st : pendingDeliveries) {
            st.setStat("DELIVERED");
            sentToService.update(st);
        }

        // Once all recipients got the message, delete from relay server

        List<SentTo> allDeliveries = List.of();
        for (Message m : pendingMessages) {
            allDeliveries = sentToService.getByMessageId(m.getmId());
            boolean allDelivered = allDeliveries.stream()
                    .allMatch(st -> st.getStat().equals("DELIVERED"));

            if (allDelivered) {
                sentToService.deleteAll(allDeliveries); // Deletes all from list
                messageService.deleteMessage(m);

            }
        }

        for (FriendRequest fr : pendingFriendRequests) {
            if(fr.getStat().equals("ACCEPTED")){
                friendRequestService.deleteFriendRequest(fr);
            }
        }

        // For group chat requests in sync we only deliver, deletion is the task of the GroupRequestMembersController


        // Delete ChatFriend, payload is one way
        for (ChatFriend cf : pendingChatFriendRequests) {
            chatFriendService.deleteChatFriend(cf);
        }

        return theResponse;
    }
}