package com.example.Controller;

import com.example.Entitiy.FriendRequest;
import com.example.Service.FriendRequest.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FriendRequestRestController {

    private final FriendRequestService friendRequestService;

    @Autowired
    public FriendRequestRestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    // POST /friend-requests → send request
    @PostMapping("/friend-requests")
    public FriendRequest sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        try {
            if (friendRequest.getId() == null || friendRequest.getId().isEmpty()) {
                friendRequest.setId(UUID.randomUUID().toString());
            }
            if (friendRequest.getCreated_at() == null) {
                friendRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
            }
            if (friendRequest.getStatus() == null) {
                friendRequest.setStatus("PENDING");
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
            @RequestBody UpdateStatusRequest statusRequest) {

        List<FriendRequest> allRequests = friendRequestService.getAllRequests();
        FriendRequest request = allRequests.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Friend request not found - " + id));

        request.setStatus(statusRequest.getStatus());
        return friendRequestService.updateFriendRequest(request);
    }

    public static class UpdateStatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}