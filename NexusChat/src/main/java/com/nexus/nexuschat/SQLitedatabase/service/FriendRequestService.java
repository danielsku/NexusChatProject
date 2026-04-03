package com.nexus.nexuschat.SQLitedatabase.service;


import com.nexus.nexuschat.SQLitedatabase.dao.contact.ContactDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.friendrequest.FriendRequestDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.identity.IdentityDAO;
import com.nexus.nexuschat.SQLitedatabase.model.Contact;
import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class FriendRequestService {

    // Consider using ENUM here to enforce type safety
    private static final Set<String> ALLOWED_STATES = Set.of("PENDING", "ACCEPTED", "DECLINED");

    private FriendRequestDAO friendRequestDAO;
    private IdentityDAO identityDAO;
    private ContactDAO contactDAO;

    @Autowired
    public FriendRequestService(FriendRequestDAO friendRequestDAO, IdentityDAO identityDAO, ContactDAO contactDAO) {
        this.friendRequestDAO = friendRequestDAO;
        this.identityDAO = identityDAO;
        this.contactDAO = contactDAO;
    }

    // Later implementation, if request already exists --> Accept request
    boolean requestExistsBetween(String senderId, String receiverId){
        return friendRequestDAO.findFriendRequestBySenderAndReceiver(senderId, receiverId) != null ||
                friendRequestDAO.findFriendRequestBySenderAndReceiver(receiverId, senderId) != null;
    }

    public boolean userIsReceiver(FriendRequest friendRequest){
        return identityDAO.findIdentity().getUserId().equals(friendRequest.getReceiverId());
    }

    // Friend requests that local user sends

    public void createFriendRequest(FriendRequest friendRequest){
        // if the same user requests are in the db, don't send it
        if(requestExistsBetween(friendRequest.getSenderId(), friendRequest.getReceiverId())){
            throw new RuntimeException("Friend request already exists");
        }

//        if(!identityDAO.findIdentity().getUserId().equals(friendRequest.getSenderId())){
//            throw new RuntimeException("Local user created friend request but is not sender");
//        }
        if(friendRequest.getSenderId().equals(friendRequest.getReceiverId())){
            throw new RuntimeException("Cannot send friend request to yourself");
        }
        friendRequest.setRequestId(UUID.randomUUID().toString());
        friendRequest.setStat("PENDING");
        friendRequestDAO.saveFriendRequest(friendRequest);
    }

    // Friend requests that local user gets
    public void storeFriendRequest(FriendRequest friendRequest){
        if(requestExistsBetween(friendRequest.getSenderId(), friendRequest.getReceiverId())){
            throw new RuntimeException("Friend request already exists");
        }
        if(!userIsReceiver(friendRequest)){
            throw new RuntimeException("Local user is not the receiver");
        }
        friendRequest.setStat("PENDING");
        friendRequestDAO.saveFriendRequest(friendRequest);
    }

    public List<FriendRequest> retrieveAllFriendRequests(){
        return friendRequestDAO.findAllFriendRequests();
    }

    // Accept friend request
    public void acceptFriendRequest(FriendRequest friendRequest, String senderUsername){
        // Validate if user is receiver
        if(!userIsReceiver(friendRequest)){
            throw new RuntimeException("Local user is not the receiver");
        }

        // Insert new friend into contact

        contactDAO.saveContact(new Contact(friendRequest.getSenderId(), senderUsername, new Timestamp(System.currentTimeMillis())));

        // Delete friendrequest
        friendRequestDAO.deleteFriendRequest(friendRequest);

    }

    // decline friend request
     public void declineFriendRequest(FriendRequest friendRequest){
        // Validate if user is receiver
         if(!userIsReceiver(friendRequest)){
             throw new RuntimeException("Local user is not the receiver");
         }

         // Delete friend request
         friendRequestDAO.deleteFriendRequest(friendRequest);
     }

     public void deleteFriendRequest(FriendRequest friendRequest){
         if(!userIsReceiver(friendRequest)){
             throw new RuntimeException("Local user is not the receiver");
         }

         // Delete friend request
         friendRequestDAO.deleteFriendRequest(friendRequest);
     }
}
