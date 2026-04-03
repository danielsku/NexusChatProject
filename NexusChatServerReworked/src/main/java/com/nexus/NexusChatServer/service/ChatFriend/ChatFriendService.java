package com.nexus.NexusChatServer.service.ChatFriend;

import com.nexus.NexusChatServer.entity.ChatFriend;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatFriendService {
    ChatFriend createChatFriend(ChatFriend chatFriend);
    ChatFriend readChatFriendById(String id);
    List<ChatFriend> findAllRequestsByReceiver_id(String userId);
    ChatFriend updateChatFriendById(ChatFriend chatFriend);
    void deleteChatFriend(ChatFriend chatFriend);
}
