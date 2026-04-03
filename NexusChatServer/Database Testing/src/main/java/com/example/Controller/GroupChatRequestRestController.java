package com.example.Controller;

import com.example.Entitiy.GroupChatRequest;
import com.example.Service.GroupChatRequest.GroupChatRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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
        if (request.getCreated_at() == null) {
            request.setCreated_at(new Timestamp(System.currentTimeMillis()));
        }
        if (request.getStat() == null) {
            request.setStat("PENDING");
        }
        return groupChatRequestService.createRequest(request);
    }

    // GET /group-requests → get received invites
    @GetMapping("/group-requests")
    public List<GroupChatRequest> getReceivedGroupRequests(@RequestParam String receiverId) {
        return groupChatRequestService.getRequestsByReceiverId(receiverId);
    }
}