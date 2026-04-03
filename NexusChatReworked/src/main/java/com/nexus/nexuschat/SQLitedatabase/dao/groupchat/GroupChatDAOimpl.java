package com.nexus.nexuschat.SQLitedatabase.dao.groupchat;


import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupChatDAOimpl implements GroupChatDAO {

    private final DataSource dataSource;

    @Autowired
    public GroupChatDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveGroupChat(GroupChat groupChat) {
        String insertQuery = "INSERT INTO group_chat(chat_id, created_by, created_at, group_name) VALUES (?, ?, ?, ?)";
        try(Connection conn = dataSource.getConnection()) {
            try {
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, groupChat.getChatId());
                    stmt.setString(2, groupChat.getCreatedBy());
                    stmt.setTimestamp(3, groupChat.getCreatedAt());
                    stmt.setString(4, groupChat.getGroupName());

                    stmt.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException(e);

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


    @Override
    public GroupChat findGroupChatById(String chatId) {
        try (Connection conn = dataSource.getConnection()) {
            String findQuery = "SELECT * FROM group_chat WHERE chat_id = ?;";

            try (PreparedStatement stmt = conn.prepareStatement(findQuery)) {
                stmt.setString(1, chatId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String createdBy = rs.getString("created_by");
                        Timestamp createdAt = rs.getTimestamp("created_at");
                        String groupName = rs.getString("group_name");

                        return new GroupChat(chatId, createdBy, createdAt, groupName);
                    } else {
                        return null;
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GroupChat> findAllGroupChats() {
        List<GroupChat> groupchats = new ArrayList<>();
        try(Connection conn = dataSource.getConnection()) {
            String findQuery = "SELECT * FROM group_chat;";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(findQuery)) {
                while (rs.next()) {
                    String chatId = rs.getString("chat_id");
                    String createdBy = rs.getString("created_by");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    String groupName = rs.getString("group_name");

                    groupchats.add(new GroupChat(chatId, createdBy, createdAt, groupName));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return groupchats;
    }

    @Override
    public GroupChat deleteGroupChat(GroupChat groupchat) {
        String deleteQuery = "DELETE FROM group_chat WHERE chat_id = ?;";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, groupchat.getChatId());
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new RuntimeException("No group chat found with id: " + groupchat.getChatId());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return groupchat;
    }
}
