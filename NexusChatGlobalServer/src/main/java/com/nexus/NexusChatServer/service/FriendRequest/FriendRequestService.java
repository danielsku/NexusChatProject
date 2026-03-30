package com.nexus.NexusChatServer.service.FriendRequest;


import com.nexus.NexusChatServer.entity.FriendRequest;

import java.util.List;

public interface FriendRequestService {
    FriendRequest createFriendRequest(FriendRequest request);
    FriendRequest updateFriendRequest(FriendRequest request);
    void deleteFriendRequest(FriendRequest request);
    List<FriendRequest> getRequestsByReceiverId(String receiver_id);
    List<FriendRequest> getRequestsBySenderId(String sender_id);
    List<FriendRequest> getAllRequests();
    FriendRequest getFriendRequestById(String id);
}