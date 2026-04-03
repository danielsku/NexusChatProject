package com.nexus.NexusChatServer.service.GroupChatRequest;


import com.nexus.NexusChatServer.entity.GroupChatRequest;

import java.util.List;

public interface GroupChatRequestService {
    GroupChatRequest createRequest(GroupChatRequest request);
    GroupChatRequest updateRequest(GroupChatRequest request);
    void deleteRequest(GroupChatRequest request);
    List<GroupChatRequest> getRequestsBySenderId(String sender_id);

    GroupChatRequest getGroupChatRequestById(String requestId);
}
