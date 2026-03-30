package com.nexus.NexusChatServer.controller.rest;

import com.nexus.NexusChatServer.controller.websocket.WebSocketSessionManager;
import com.nexus.NexusChatServer.entity.FriendRequest;
import com.nexus.NexusChatServer.service.FriendRequest.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

// TODO: For every Controller, use the DTO/POJO model instead of the JPA entity

@RestController
@RequestMapping("/api")
public class FriendRequestRestController {

    private final FriendRequestService friendRequestService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public FriendRequestRestController(FriendRequestService friendRequestService, SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager) {
        this.friendRequestService = friendRequestService;
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    // POST /friend-requests → send request
    @PostMapping("/friend-requests")
    public FriendRequest sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        try {
            if (friendRequest.getRequestId() == null || friendRequest.getRequestId().isEmpty()) {
                friendRequest.setRequestId(UUID.randomUUID().toString());
            }
            if (friendRequest.getCreatedAt() == null) {
                friendRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }
            if (friendRequest.getStat() == null) {
                friendRequest.setStat("PENDING");
            }
            return friendRequestService.createFriendRequest(friendRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create friend request: " + e.getMessage());
        }
    }

    // GET /friend-requests → get received requests
    @GetMapping("/friend-requests/{receiverId}")
    public List<FriendRequest> getReceivedFriendRequests(@PathVariable String receiverId) {
        return friendRequestService.getRequestsByReceiverId(receiverId);
    }

    @GetMapping("/friend-requests")
    public List<FriendRequest> getReceivedFriendRequests() {
        return friendRequestService.getAllRequests();
    }

    // PUT /friend-requests/{id} → update status
    @PutMapping("/friend-requests/{id}")
    public FriendRequest updateFriendRequestStatus(
            @PathVariable String id,
            @RequestBody FriendRequest friendRequestPayload) {

        // fetch existing from DB
        FriendRequest existingRequest = friendRequestService.getFriendRequestById(id);
        if (existingRequest == null) {
            throw new RuntimeException("Friend with id - " + id + " not found");
        }

        // only update allowed fields
        existingRequest.setStat(friendRequestPayload.getStat());
        existingRequest.setUsername(friendRequestPayload.getUsername());

        // save and update
        FriendRequest updatedRequest = friendRequestService.updateFriendRequest(existingRequest);

        // send **updatedRequest**, not the client payload, to WebSocket
        if (sessionManager.getActiveUsernames().contains(existingRequest.getSenderId())) {
            messagingTemplate.convertAndSend(
                    "/topic/friend-requests/" + existingRequest.getSenderId(),
                    updatedRequest
            );
        }

        return updatedRequest;
    }

    // DELETE /friend-requests/{id} → delete a friend request
    @DeleteMapping("/friend-requests/{id}")
    public void deleteFriendRequest(@PathVariable String id) {

        FriendRequest request = friendRequestService.getFriendRequestById(id);

        if(request == null){
            throw new RuntimeException("Friend with id - " + id + " not found");
        }

        // Add security later, only sender or receiver can delete request
//        if(!request.getSenderId().equals(currentUserId) && !request.getReceiverId().equals(currentUserId)) {
//            throw new RuntimeException("Not authorized to delete this request");
//        }

        friendRequestService.deleteFriendRequest(request);
    }

}