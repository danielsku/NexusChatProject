package com.nexus.NexusChatServer.repository;

import com.nexus.NexusChatServer.entity.MessageDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery, String> {

    // Find a MessageDelivery placeholder by user ID
    MessageDelivery findByReceiverId(String userId);
}