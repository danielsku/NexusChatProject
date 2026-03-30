package com.nexus.nexuschat.SQLitedatabase.dao.friendrequest;


import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;

import java.util.List;

public interface FriendRequestDAO {

    // Create
    void saveFriendRequest(FriendRequest friendRequest);

    // Read
    FriendRequest findFriendRequestById(String requestId);   // Get a specific request
    List<FriendRequest> findAllFriendRequests();             // Get all requests
    List<FriendRequest> findRequestsBySender(String senderId); // Requests sent by a user
    List<FriendRequest> findRequestsByReceiver(String receiverId); // Requests received by a user
    FriendRequest findFriendRequestBySenderAndReceiver(String senderId, String receiverId);

    // Update
    void updateFriendRequest(FriendRequest friendRequest); // For example, to update status (accepted/declined)

    // Delete
    FriendRequest deleteFriendRequest(FriendRequest friendRequest); // Remove a request
}
