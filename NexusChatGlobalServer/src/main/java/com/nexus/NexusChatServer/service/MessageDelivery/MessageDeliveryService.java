package com.nexus.NexusChatServer.service.MessageDelivery;


import com.nexus.NexusChatServer.entity.MessageDelivery;

public interface MessageDeliveryService {
    MessageDelivery create(MessageDelivery delivery);
    void delete(MessageDelivery delivery);
    MessageDelivery getById(String receiverId);
}