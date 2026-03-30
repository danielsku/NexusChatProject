package com.nexus.NexusChatServer.repository;

import com.nexus.NexusChatServer.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    // find all missed messages through sentTo
    @Query("SELECT m FROM Message m JOIN m.deliveries d WHERE d.receiver.receiverId = :userId AND d.stat = 'PENDING'")
    List<Message> findMessagesByUserId(@Param("userId") String userId);
}