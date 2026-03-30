package com.example.Service.SentTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.DAO.SentToRepository;
import com.example.Entitiy.SentTo;
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
    public List<SentTo> getByMessageId(String messageId) {
        return repo.findByMessageId(messageId);
    }

    @Override
    public List<SentTo> getByReceiverId(String receiverId) {
        return repo.findByReceiverId(receiverId);
    }
}