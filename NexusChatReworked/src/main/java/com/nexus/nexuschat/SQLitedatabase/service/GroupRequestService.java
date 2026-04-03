package com.nexus.nexuschat.SQLitedatabase.service;

import com.nexus.nexuschat.SQLitedatabase.dao.chatmember.ChatMemberDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.contact.ContactDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.grouprequest.GroupRequestDAO;
import com.nexus.nexuschat.SQLitedatabase.dao.identity.IdentityDAO;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupRequestService {

    private GroupRequestDAO groupRequestDAO;
    private IdentityDAO identityDAO;
    private ChatMemberDAO chatMemberDAO;
    private ContactDAO contactDAO;

    @Autowired
    public GroupRequestService(GroupRequestDAO groupRequestDAO, IdentityDAO identityDAO, ChatMemberDAO chatMemberDAO, ContactDAO contactDAO) {
        this.groupRequestDAO = groupRequestDAO;
        this.identityDAO = identityDAO;
        this.chatMemberDAO = chatMemberDAO;
        this.contactDAO = contactDAO;
    }

    // Only for testing purposes
    public void createGroupRequest(GroupRequest groupRequest){

        if (groupRequest.getReceiverId() == null) {
            groupRequest.setReceiverId(identityDAO.findIdentity().getUserId());
        }
        ;
        groupRequestDAO.saveGroupRequest(groupRequest);
    }

    // Not needed for now
    public GroupRequest getGroupRequestById(String requestId){
        return null;
    }

    public List<GroupRequest> getAllGroupRequests(){
        return groupRequestDAO.findAllGroupRequests();
    }

    // TODO: Implement core logic later
    public void acceptGroupRequest(String requestId, String receiverId){

    }

    // TODO: Implement core logic later
    public void declineGroupRequest(String requestId, String receiverId){

    }

    // TODO: Implement core logic later
    public void deleteGroupRequest(String requestId){
        groupRequestDAO.deleteGroupRequest(groupRequestDAO.findGroupRequestById(requestId));
    }

}
