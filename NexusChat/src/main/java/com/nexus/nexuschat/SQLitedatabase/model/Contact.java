package com.nexus.nexuschat.SQLitedatabase.model;

import java.sql.Timestamp;

public class Contact {

    private String contactId;
    private String username;
    private Timestamp addedAt;

    public Contact() {
    }

    public Contact(String contactId, String username, Timestamp addedAt) {
        this.contactId = contactId;
        this.username = username;
        this.addedAt = addedAt;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp  getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId='" + contactId + '\'' +
                ", username='" + username + '\'' +
                ", addedAt='" + addedAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact c = (Contact) o;
        return contactId.equals(c.contactId);
    }

    @Override
    public int hashCode() {
        return contactId.hashCode();
    }
}
