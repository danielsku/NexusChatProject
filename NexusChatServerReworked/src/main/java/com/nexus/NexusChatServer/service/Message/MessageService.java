package com.nexus.NexusChatServer.service.Message;


import com.nexus.NexusChatServer.entity.Message;

import java.util.List;
//import java.util.Optional;

public interface MessageService {
    Message createMessage(Message message);
    Message updateMessage(Message message);
    void deleteMessage(Message message);
    List<Message> getMessagesByChatId(String chatId);
}
