package com.nexus.nexuschat.SQLitedatabase.dao.grouprequest;

import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;

import java.util.List;

public interface GroupRequestDAO {


    // Create
    void saveGroupRequest(GroupRequest groupRequest);

    // Read
    GroupRequest findGroupRequestById(String requestId);   // Get a specific request
    List<GroupRequest> findAllGroupRequests();             // Get all requests
    List<GroupRequest> findRequestsBySender(String senderId); // Requests sent by a user

    // Update
    void updateGroupRequest(GroupRequest groupRequest); // For example, to update status (accepted/declined)

    // Delete
    GroupRequest deleteGroupRequest(GroupRequest groupRequest); // Remove a request
    
}
