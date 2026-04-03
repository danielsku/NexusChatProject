package com.nexus.NexusChatServer.service.ChatFriend;

import com.nexus.NexusChatServer.entity.ChatFriend;
import com.nexus.NexusChatServer.repository.ChatFriendRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatFriendServiceImpl implements ChatFriendService{
    private final ChatFriendRespository chatFriendRespository;

    @Autowired
    public ChatFriendServiceImpl(ChatFriendRespository chatFriendRespository){
        this.chatFriendRespository = chatFriendRespository;
    }

    @Override
    public ChatFriend createChatFriend(ChatFriend chatFriend) {
        return chatFriendRespository.save(chatFriend);
    }

    @Override
    public ChatFriend readChatFriendById(String id) {
        Optional<ChatFriend> result = chatFriendRespository.findById(id);
        ChatFriend theChatFriend = null;
        if(result.isPresent()){
            theChatFriend = result.get();
        }
        return theChatFriend;
    }

    @Override
    public List<ChatFriend> findAllRequestsByReceiver_id(String userId) {
        return chatFriendRespository.findRequestsByReceiver_id(userId);
    }

    // Not needed
    @Override
    public ChatFriend updateChatFriendById(ChatFriend chatFriend) {
        return null;
    }

    @Override
    public void deleteChatFriend(ChatFriend chatFriend) {
        chatFriendRespository.delete(chatFriend);
    }
}
