package com.nexus.NexusChatServer.service.Message;

import com.nexus.NexusChatServer.entity.Message;
import com.nexus.NexusChatServer.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository repo;

    @Autowired
    public MessageServiceImpl(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public Message createMessage(Message message) {
        return repo.save(message);
    }

    @Override
    public Message updateMessage(Message message) {
        Optional<Message> existMessage = repo.findById(message.getmId());
        if (existMessage.isEmpty()) {
            throw new RuntimeException("Message not found with id " + message.getmId());
        }
        Message exMessage = existMessage.get();

        if (message.getContent() != null && !message.getContent().isEmpty()) {
            exMessage.setContent(message.getContent());
        }
        if (message.getChatId() != null && !message.getChatId().isEmpty()) {
            exMessage.setChatId(message.getChatId());
        }
        if (message.getUserId() != null && !message.getUserId().isEmpty()) {
            exMessage.setUserId(message.getUserId());
        }
        if (message.getSentAt() != null) {
            exMessage.setSentAt(message.getSentAt());
        }

        return repo.save(exMessage);
    }

    @Override
    public void deleteMessage(Message message) {
        repo.delete(message);
    }

    @Override
    public List<Message> getMessagesByChatId(String chatId) {
        List<Message> allMessages = repo.findAll();
        return allMessages.stream().filter(message -> message.getChatId().equals(chatId))
                .collect(Collectors.toList());
    }
}
