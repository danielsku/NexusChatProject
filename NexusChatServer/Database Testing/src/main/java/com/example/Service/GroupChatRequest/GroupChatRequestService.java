package com.example.Service.GroupChatRequest;

import com.example.Entitiy.*;
import java.util.List;

public interface GroupChatRequestService {
    GroupChatRequest createRequest(GroupChatRequest request);
    GroupChatRequest updateRequest(GroupChatRequest request);
    void deleteRequest(GroupChatRequest request);
    List<GroupChatRequest> getRequestsByReceiverId(String receiver_id);
    List<GroupChatRequest> getRequestsBySenderId(String sender_id);
}
