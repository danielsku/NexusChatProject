package com.nexus.NexusChatServer.service.GroupRequestMembers;


import com.nexus.NexusChatServer.entity.GroupRequestMembers;

import java.util.List;

public interface GroupRequestMembersService {
    GroupRequestMembers createMember(GroupRequestMembers member);
    GroupRequestMembers updateMember(GroupRequestMembers member);
    void deleteMember(GroupRequestMembers member);
    List<GroupRequestMembers> getMembersByRequestId(String request_id);
    List<GroupRequestMembers> getRequestIdByMember(String receiverId);
}