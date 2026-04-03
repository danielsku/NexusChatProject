package com.nexus.NexusChatServer.service.SentTo;

import com.nexus.NexusChatServer.entity.SentTo;

import java.util.List;

public interface SentToService {
    SentTo create(SentTo sentTo);
    void delete(SentTo sentTo);
    void update(SentTo sentTo);
    List<SentTo> getByMessageId(String messageId);
    List<SentTo> getByReceiverId(String receiverId);
    void deleteAll(List<SentTo> allDeliveries);
}