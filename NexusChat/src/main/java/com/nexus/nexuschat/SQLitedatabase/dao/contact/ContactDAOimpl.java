package com.nexus.nexuschat.SQLitedatabase.dao.contact;

import com.nexus.nexuschat.SQLitedatabase.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContactDAOimpl implements ContactDAO{

    private final DataSource dataSource;

    @Autowired
    public ContactDAOimpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveContact(Contact contact) {
        String insertQuery = "INSERT INTO contact(contact_id, username, added_at) VALUES (?, ?, ?);";
        try(Connection conn = dataSource.getConnection()) {
            try {
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement insertMessage = conn.prepareStatement(insertQuery);) {
                    insertMessage.setString(1, contact.getContactId());
                    insertMessage.setString(2, contact.getUsername());
                    insertMessage.setTimestamp(3, contact.getAddedAt());
                    insertMessage.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save contact: " + contact, e);
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
    public void updateContact(Contact contact) {
        String updateQuery = "UPDATE contact SET username = ? WHERE contact_id = ?";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, contact.getUsername());
                stmt.setString(2, contact.getContactId());
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new RuntimeException("No contact found with id: " + contact.getContactId());
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database Error", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Contact> findAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String findQuery = "SELECT * FROM contact;";
        try(Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(findQuery)) {
                    while (rs.next()) {
                        String contactId = rs.getString("contact_id");
                        String username = rs.getString("username");
                        Timestamp addedAt = rs.getTimestamp("added_at");
                        contacts.add(new Contact(contactId, username, addedAt));
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contacts;
    }

    // Could use Optional<Contact> later on
    @Override
    public Contact findContactById(String chatId) {
        String findQuery = "SELECT * FROM contact WHERE contact_id = ?;";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(findQuery)) {
            stmt.setString(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // move to the first row
                    String id = rs.getString("contact_id");
                    String username = rs.getString("username");
                    Timestamp addedAt = rs.getTimestamp("added_at");
                    return new Contact(id, username, addedAt);
                } else {
                    return null; // no contact found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Contact deleteContact(Contact contact) {
        String deleteQuery = "DELETE FROM contact WHERE contact_id = ?";
        try(Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, contact.getContactId());
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new RuntimeException("No contact found with id: " + contact.getContactId());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return contact;
    }
}
