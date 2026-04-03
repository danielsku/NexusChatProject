package com.example.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Entitiy.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    // find all missed messages through sentTo
    @Query("SELECT m FROM Message m JOIN m.deliveries d WHERE d.receiver.receiverId = :userId AND d.stat = 'PENDING'")
    List<Message> findMessagesByUserId(@Param("userId") String userId);
}