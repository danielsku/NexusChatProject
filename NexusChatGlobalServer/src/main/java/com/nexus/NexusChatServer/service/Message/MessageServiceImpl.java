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
        Optional<Message> existMessage = repo.findById(message.getMessage_id());
        if (existMessage.isEmpty()) {
            throw new RuntimeException("Message not found with id " + message.getMessage_id());
        }
        Message exMessage = existMessage.get();

        if (message.getContent() != null && !message.getContent().isEmpty()) {
            exMessage.setContent(message.getContent());
        }
        if (message.getChat_id() != null && !message.getChat_id().isEmpty()) {
            exMessage.setChat_id(message.getChat_id());
        }
        if (message.getSender_id() != null && !message.getSender_id().isEmpty()) {
            exMessage.setSender_id(message.getSender_id());
        }
        if (message.getSent_at() != null) {
            exMessage.setSent_at(message.getSent_at());
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
        return allMessages.stream().filter(message -> message.getChat_id().equals(chatId))
                .collect(Collectors.toList());
    }
}
