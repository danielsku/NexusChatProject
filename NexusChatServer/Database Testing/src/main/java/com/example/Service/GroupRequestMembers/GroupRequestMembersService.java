package com.example.Service.GroupRequestMembers;

import com.example.Entitiy.*;
import java.util.List;

public interface GroupRequestMembersService {
    GroupRequestMembers createMember(GroupRequestMembers member);
    GroupRequestMembers updateMember(GroupRequestMembers member);
    void deleteMember(GroupRequestMembers member);
    List<GroupRequestMembers> getMembersByRequestId(String request_id);
}