package com.nexus.NexusChatServer.service.SentTo;

import com.nexus.NexusChatServer.entity.SentTo;
import com.nexus.NexusChatServer.repository.SentToRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentToServiceImpl implements SentToService {

    private final SentToRepository repo;

    @Autowired
    public SentToServiceImpl(SentToRepository repo) {
        this.repo = repo;
    }

    @Override
    public SentTo create(SentTo sentTo) {
        return repo.save(sentTo);
    }

    @Override
    public void delete(SentTo sentTo) {
        repo.delete(sentTo);
    }

    @Override
    public void update(SentTo sentTo) {
        repo.save(sentTo); // Just updates the entity for an existing id
    }

    @Override
    public List<SentTo> getByMessageId(String messageId) {
        return repo.findByMessageId(messageId);
    }

    @Override
    public List<SentTo> getByReceiverId(String receiverId) {
        return repo.findByReceiverId(receiverId);
    }

    @Override
    public void deleteAll(List<SentTo> allDeliveries) {
        for(SentTo s : allDeliveries){
            repo.delete(s);
        }
    }
}