package com.nexus.NexusChatServer.repository;

import com.nexus.NexusChatServer.entity.GroupRequestMembers;
import com.nexus.NexusChatServer.entity.GroupRequestMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRequestMemberRepository extends JpaRepository<GroupRequestMembers, GroupRequestMembersId>{
    
}
