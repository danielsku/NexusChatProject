package com.nexus.nexuschat.SQLitedatabase.dao.contact;



import com.nexus.nexuschat.SQLitedatabase.model.Contact;

import java.util.List;

public interface ContactDAO {

    // Create
    public void saveContact(Contact contact);

    // Read
    public List<Contact> findAllContacts();
    public Contact findContactById(String chatId);

    // Update
    public void updateContact(Contact contact);

    // Delete
    public Contact deleteContact(Contact contact);
}
