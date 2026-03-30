package com.nexus.NexusChatServer.repository;

import com.nexus.NexusChatServer.entity.GroupChatRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRequestRepository extends JpaRepository<GroupChatRequest, String>{

}
