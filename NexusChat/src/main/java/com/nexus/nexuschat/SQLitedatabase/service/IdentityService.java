package com.nexus.nexuschat.SQLitedatabase.service;


import com.nexus.nexuschat.SQLitedatabase.dao.identity.IdentityDAO;
import com.nexus.nexuschat.SQLitedatabase.model.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentityService {

    private IdentityDAO identityDAO;

    @Autowired
    public IdentityService(IdentityDAO identityDAO) {
        this.identityDAO = identityDAO;
    }

    public Identity createIdentity(){
        if(identityDAO.findIdentity() != null){
            throw new RuntimeException("Local user already exists");
        }

        Identity theIdentity = new Identity(UUID.randomUUID().toString());
        identityDAO.saveIdentity(theIdentity);
        return theIdentity;
    }

    public Identity readIdentity(){
        return identityDAO.findIdentity();
    }

    public void removeIdentity(Identity identity){
        identityDAO.deleteIdentity(identity);
    }
}
