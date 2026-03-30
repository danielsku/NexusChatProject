package com.nexus.nexuschat.SQLitedatabase.dao.friendrequest;

import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FriendRequestDAOimpl implements FriendRequestDAO{

    private final DataSource dataSource;

    public FriendRequestDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    };

    @Override
    public void saveFriendRequest(FriendRequest friendRequest) {
        String insertQuery = "INSERT INTO friend_requests(request_id, sender_id, receiver_id, username, stat, created_at) VALUES (?, ?, ?, ?, ?, ?);";
        try(Connection conn = dataSource.getConnection()) {
            try {
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement insertMessage = conn.prepareStatement(insertQuery);) {
                    insertMessage.setString(1, friendRequest.getRequestId());
                    insertMessage.setString(2, friendRequest.getSenderId());
                    insertMessage.setString(3, friendRequest.getReceiverId());
                    insertMessage.setString(4, friendRequest.getUsername());
                    insertMessage.setString(5, friendRequest.getStat());
                    insertMessage.setTimestamp(6, friendRequest.getCreatedAt());
                    insertMessage.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save message: " + friendRequest, e);
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
    public FriendRequest findFriendRequestById(String requestId) {
        return null;
    }

    @Override
    public List<FriendRequest> findAllFriendRequests() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        try(Connection conn = dataSource.getConnection()) {
            String findQuery = "SELECT * FROM friend_requests;";
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(findQuery)) {
                    while (rs.next()) {
                        String requestId = rs.getString("request_id");
                        String senderId = rs.getString("sender_id");
                        String receiverId = rs.getString("receiver_id");
                        String username = rs.getString("username");
                        String status = rs.getString("stat");
                        Timestamp createdAt = rs.getTimestamp("created_at");
                        friendRequests.add(new FriendRequest(requestId, senderId, receiverId, username, status, createdAt));
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendRequests;
    }

    @Override
    public List<FriendRequest> findRequestsBySender(String senderId) {
        return List.of();
    }

    // Implement later
    @Override
    public List<FriendRequest> findRequestsByReceiver(String receiverId) {
        return List.of();
    }

    @Override
    public FriendRequest findFriendRequestBySenderAndReceiver(String senderId, String receiverId) {
        try(Connection conn = dataSource.getConnection()) {
            String findQuery = "SELECT * FROM friend_requests WHERE sender_id = ? AND receiver_id = ?;";
            try (PreparedStatement stmt = conn.prepareStatement(findQuery)) {
                stmt.setString(1, senderId);
                stmt.setString(2, receiverId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        String requestId = rs.getString("request_id");
                        String username = rs.getString("username");
                        String status = rs.getString("stat");
                        Timestamp createdAt = rs.getTimestamp("created_at");
                        return new FriendRequest(requestId, senderId, receiverId, username, status, createdAt);
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
    public void updateFriendRequest(FriendRequest friendRequest) {
        String updateQuery = "UPDATE friend_requests SET stat = ? WHERE request_id = ?";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, friendRequest.getStat());
                stmt.setString(2, friendRequest.getRequestId());
                int rows = stmt.executeUpdate();
                if(rows == 0){
                    throw new RuntimeException("No friend request found :" + friendRequest);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FriendRequest deleteFriendRequest(FriendRequest friendRequest) {
        String deleteQuery = "DELETE FROM friend_requests WHERE request_id = ?";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, friendRequest.getRequestId());
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new RuntimeException("No friend request found with id: " + friendRequest.getRequestId());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendRequest;

    }
}
