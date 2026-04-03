package com.example.Service.SentTo;

import com.example.Entitiy.SentTo;
import java.util.List;

public interface SentToService {
    SentTo create(SentTo sentTo);
    void delete(SentTo sentTo);
    List<SentTo> getByMessageId(String messageId);
    List<SentTo> getByReceiverId(String receiverId);
}