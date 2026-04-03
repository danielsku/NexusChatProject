package com.nexus.nexuschat.SQLitedatabase.service;


import com.nexus.nexuschat.SQLitedatabase.dao.contact.ContactDAO;
import com.nexus.nexuschat.SQLitedatabase.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ContactService {

    private ContactDAO contactDAO;

    @Autowired
    public ContactService(ContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }

    public Contact createContact(String contactId, String username){
        if (contactId == null || username == null || username.isBlank()) {
            throw new RuntimeException("Invalid contact data");
        }

        if(contactDAO.findContactById(contactId) != null){
//            throw new RuntimeException("Contact already exists : " + contactId);
            System.out.println("Contact already exists!");
            return null;
        }

        Contact theContact = new Contact(contactId, username, new Timestamp(System.currentTimeMillis()));

        contactDAO.saveContact(theContact);

        return theContact;
    }

    public List<Contact> retrieveAllContacts(){

        List<Contact> theContacts = contactDAO.findAllContacts();

        if (theContacts.isEmpty()) {
            throw new RuntimeException("Contact list is empty");
        }

        return theContacts;
    }

    public Contact retrieveContactById(String contactId){
        Contact theContact = contactDAO.findContactById(contactId);

//        if (theContact == null) {
//            throw new RuntimeException("Did not find contact id - " + contactId);
//        }

        return theContact;
    }

    public Contact removeContact(Contact contact){
        Contact theContact = contactDAO.findContactById(contact.getContactId());

        if (theContact == null) {
            throw new RuntimeException("Did not find contact id - " + contact.getContactId());
        }

        return contactDAO.deleteContact(contact);
    }

}
