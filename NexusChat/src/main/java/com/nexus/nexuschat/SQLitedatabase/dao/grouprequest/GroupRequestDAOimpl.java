package com.nexus.nexuschat.SQLitedatabase.dao.grouprequest;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupRequestDAOimpl implements GroupRequestDAO{

    private final DataSource dataSource;

    public GroupRequestDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveGroupRequest(GroupRequest groupRequest) {
        String insertQuery = "INSERT INTO group_request(request_id, sender_id, receiver_id, chat_name, stat, created_at) VALUES (?, ?, ?, ?, ?, ?);";
        try(Connection conn = dataSource.getConnection()) {
            try {
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement insertMessage = conn.prepareStatement(insertQuery);) {
                    insertMessage.setString(1, groupRequest.getRequestId());
                    insertMessage.setString(2, groupRequest.getSenderId());
                    insertMessage.setString(3, groupRequest.getReceiverId());
                    insertMessage.setString(4, groupRequest.getChatName());
                    insertMessage.setString(5, groupRequest.getStat());
                    insertMessage.setTimestamp(6, groupRequest.getCreatedAt());
                    insertMessage.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save group request: " + groupRequest, e);
                } finally {
                    conn.setAutoCommit(oldAutoCommit);
                }

            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Implement later
    @Override
    public GroupRequest findGroupRequestById(String requestId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<GroupRequest> findAllGroupRequests() {
        List<GroupRequest> groupRequests = new ArrayList<>();
        try(Connection conn = dataSource.getConnection()) {
            String findQuery = "SELECT * FROM group_request;";
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(findQuery)) {
                    while (rs.next()) {
                        String requestId = rs.getString("request_id");
                        String senderId = rs.getString("sender_id");
                        String receiverId = rs.getString("receiver_id");
                        String chatName = rs.getString("chat_name");
                        String stat = rs.getString("stat");
                        Timestamp createdAt = rs.getTimestamp("created_at");
                        groupRequests.add(new GroupRequest(requestId, senderId, receiverId, chatName, stat, createdAt));
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return groupRequests;
    }

    // implement later
    @Override
    public List<GroupRequest> findRequestsBySender(String senderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // implement later
    @Override
    public void updateGroupRequest(GroupRequest groupRequest) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public GroupRequest deleteGroupRequest(GroupRequest groupRequest) {
        String deleteQuery = "DELETE FROM group_request WHERE request_id = ?";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, groupRequest.getRequestId());
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new RuntimeException("No group request found with id: " + groupRequest.getRequestId());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return groupRequest;
    }
}
