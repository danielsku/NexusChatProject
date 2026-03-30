package com.example.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Entitiy.MessageDelivery;

@Repository
public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery, String> {

    // Find a MessageDelivery placeholder by user ID
    MessageDelivery findByReceiverId(String userId);
}