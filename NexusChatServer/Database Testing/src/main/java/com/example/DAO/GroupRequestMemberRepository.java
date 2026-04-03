package com.example.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Entitiy.*;

@Repository
public interface GroupRequestMemberRepository extends JpaRepository<GroupRequestMembers, GroupRequestMembersId>{
    
}
