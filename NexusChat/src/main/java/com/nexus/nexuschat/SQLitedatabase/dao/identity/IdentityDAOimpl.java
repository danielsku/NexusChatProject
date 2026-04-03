package com.nexus.nexuschat.SQLitedatabase.dao.identity;

import com.nexus.nexuschat.SQLitedatabase.model.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class IdentityDAOimpl implements IdentityDAO{

    private final DataSource dataSource;

    @Autowired
    public IdentityDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveIdentity(Identity identity) {
        String insertQuery = "INSERT INTO identity VALUES (?)";
        try(Connection conn = dataSource.getConnection()) {
            try {
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setString(1, identity.getUserId());
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

    // No need, Id is permanent
    @Override
    public void updateIdentity(Identity identity) {

    }

    @Override
    public Identity findIdentity() {
        String findQuery = "SELECT * FROM identity;";
        try(Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(findQuery);
                if (rs.next()) {
                    return new Identity(rs.getString("user_id"));
                } else {
                    return null; // Or throw exception if you expect always one identity
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteIdentity(Identity identity) {
        String deleteQuery = "DELETE FROM identity;";
        try(Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteQuery);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
