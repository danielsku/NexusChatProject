package com.nexus.nexuschat.SQLitedatabase.dao.chatmember;

import com.nexus.nexuschat.SQLitedatabase.model.ChatMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatMemberDAOimpl implements ChatMemberDAO {

    private final DataSource dataSource;

    @Autowired
    public ChatMemberDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveChatMember(ChatMember chatMember) {
        String insertQuery = "INSERT OR IGNORE INTO chat_member(chat_id, user_id) VALUES (?, ?);";
        try(Connection conn = dataSource.getConnection()) {
            try {
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement insertMessage = conn.prepareStatement(insertQuery);) {
                    insertMessage.setString(1, chatMember.getChatId());
                    insertMessage.setString(2, chatMember.getUserId());
                    int rows = insertMessage.executeUpdate();
                    System.out.println("Rows inserted: " + rows);
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();

                    if ("23000".equals(e.getSQLState()) ||
                            (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique"))) { // Duplicate detection in SQLite
//                        throw new RuntimeException("User is already a member of this chat: " + chatMember);
                        System.out.println("User is already a member of this chat");
                        return;
                    }

                    throw new RuntimeException("Failed to save chat member: " + chatMember, e);
                } finally {
                    conn.setAutoCommit(oldAutoCommit);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatMember> executeChatMemberQuery(PreparedStatement stmt) throws SQLException {
        List<ChatMember> members = new ArrayList<>();
        try(ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                String cId = rs.getString("chat_id");
                String uId = rs.getString("user_id");
                members.add(new ChatMember(cId, uId));
            }
        }
        return members;
    }

    @Override
    public List<ChatMember> findAllMembersByChat(String chatId) {
        String findQuery = "SELECT * FROM chat_member WHERE chat_id = ?;";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(findQuery)) {
                stmt.setString(1, chatId);
                return executeChatMemberQuery(stmt);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChatMember> findAllChatsByUser(String userId) {
        String findQuery = "SELECT * FROM chat_member WHERE user_id = ?;";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(findQuery)) {
                stmt.setString(1, userId);
                return executeChatMemberQuery(stmt);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean findChatMember(String chatId, String userId) {
        String findQuery = "SELECT 1 FROM chat_member WHERE chat_id = ? AND user_id = ?;";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(findQuery)) {

                stmt.setString(1, chatId);
                stmt.setString(2, userId);

                try(ResultSet rs = stmt.executeQuery()){
                    return rs.next();
                }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatMember removeChatMember(ChatMember chatMember) {
        String deleteQuery = "DELETE FROM chat_member WHERE chat_id = ? AND user_id = ?;";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, chatMember.getChatId());
                stmt.setString(2, chatMember.getUserId());
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new RuntimeException("No such chat member: " + chatMember);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return chatMember;
    }

    @Override
    public void removeAllMembersFromChat(String chatId) {
        String deleteQuery = "DELETE FROM chat_member WHERE chat_id = ?;";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, chatId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
