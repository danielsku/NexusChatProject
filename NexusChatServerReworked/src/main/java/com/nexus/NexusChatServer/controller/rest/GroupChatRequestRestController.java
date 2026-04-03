package com.nexus.NexusChatServer.controller.rest;

import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.service.GroupChatRequest.GroupChatRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.UUID;

// TODO: For every Controller, use the DTO/POJO model instead of the JPA entity

@RestController
@RequestMapping("/api")
public class GroupChatRequestRestController {

    private final GroupChatRequestService groupChatRequestService;

    @Autowired
    public GroupChatRequestRestController(GroupChatRequestService groupChatRequestService) {
        this.groupChatRequestService = groupChatRequestService;
    }

    // POST /group-requests → invite user
    @PostMapping("/group-requests")
    public GroupChatRequest inviteToGroup(@RequestBody GroupChatRequest request) {
        if (request.getId() == null || request.getId().isEmpty()) {
            request.setId(UUID.randomUUID().toString());
        }
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        return groupChatRequestService.createRequest(request);
    }

    // GET /group-requests → get received invites
//    @GetMapping("/group-requests")
//    public List<GroupChatRequest> getReceivedGroupRequests(@RequestParam String receiverId) {
//        return groupChatRequestService.getRequestsByReceiverId(receiverId);
//    }
}