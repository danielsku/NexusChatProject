package com.nexus.nexuschat.SQLitedatabase.dao.message;

import com.nexus.nexuschat.SQLitedatabase.model.Message;

import java.sql.Timestamp;
import java.util.List;

public interface MessageDAO {

    // Create
    public void saveMessage(Message message);

    // Read
    public List<Message> findMessageByLatest(String chatId, int limit, Timestamp latestTime, String mId);
    public List<Message> findMessageBeforeTime(String chatId, Timestamp timestamp, int limit);
    public List<Message> findMessagesAfterTime(String chatId, Timestamp timestamp);
}
