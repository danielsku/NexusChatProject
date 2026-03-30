package com.nexus.nexuschat.SQLitedatabase.dao.message;

import com.nexus.nexuschat.SQLitedatabase.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

@Repository
public class MessageDAOimpl implements MessageDAO{

    private final DataSource dataSource;

    @Autowired
    public MessageDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveMessage(Message message) {
        String insertQuery = "INSERT INTO message(m_id, chat_id, user_id, content, sent_at) VALUES (?, ?, ?, ?, ?);";
        try(Connection conn = dataSource.getConnection()) {
            try{
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement insertMessage = conn.prepareStatement(insertQuery);) {
                    insertMessage.setString(1, message.getmId());
                    insertMessage.setString(2, message.getChatId());
                    insertMessage.setString(3, message.getUserId());
                    insertMessage.setString(4, message.getContent());
                    insertMessage.setTimestamp(5, message.getSentAt());
                    insertMessage.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save message: " + message, e);
                } finally {
                    conn.setAutoCommit(oldAutoCommit);
                }

            }catch(SQLException e) {
                throw new RuntimeException("Database error", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Message> executeMessageQuery(PreparedStatement stmt) throws SQLException {
        List<Message> messages = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String mId = rs.getString("m_id");
                    String chatId = rs.getString("chat_id");
                    String userId = rs.getString("user_id");
                    String content = rs.getString("content");
                    Timestamp sentAt = rs.getTimestamp("sent_at");
                    messages.add(new Message(mId, chatId, userId, content, sentAt));
                }
            }
        return messages;
    }

    @Override
    public List<Message> findMessageByLatest(String chatId, int limit, Timestamp latestTime, String mId) {
        String findQuery = "SELECT * FROM message WHERE chat_id = ? AND (sent_at < ? OR (sent_at = ? AND m_id < ?)) ORDER BY sent_at DESC LIMIT ?";
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(findQuery)){
                stmt.setString(1, chatId);
                stmt.setTimestamp(2, latestTime);
                stmt.setTimestamp(3, latestTime);
                stmt.setString(4, mId);
                stmt.setInt(5, limit);
                return executeMessageQuery(stmt);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Message> findMessageBeforeTime(String chatId, Timestamp timestamp, int limit) {
        String findQuery = "SELECT * FROM message WHERE chat_id = ? AND sent_at < ? ORDER BY sent_at DESC LIMIT ?";
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(findQuery)){
                stmt.setString(1, chatId);
                stmt.setTimestamp(2, timestamp);
                stmt.setInt(3, limit);
                return executeMessageQuery(stmt);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Needed only for offline sync
    @Override
    public List<Message> findMessagesAfterTime(String chatId, Timestamp timestamp) {
        String findQuery = "SELECT * FROM message WHERE chat_id = ? AND sent_at > ? ORDER BY sent_at DESC";
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(findQuery)){
                stmt.setString(1, chatId);
                stmt.setTimestamp(2, timestamp);
                return executeMessageQuery(stmt);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
