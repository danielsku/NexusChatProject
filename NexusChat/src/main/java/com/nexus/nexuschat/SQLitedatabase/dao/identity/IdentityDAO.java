package com.nexus.nexuschat.SQLitedatabase.dao.identity;

import com.nexus.nexuschat.SQLitedatabase.model.Identity;

public interface IdentityDAO {

    // Create
    void saveIdentity(Identity identity);

    // Update
    void updateIdentity(Identity identity);

    // Read
    Identity findIdentity();

    // Delete
    void deleteIdentity(Identity identity);

}
