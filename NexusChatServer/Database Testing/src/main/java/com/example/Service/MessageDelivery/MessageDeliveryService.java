package com.example.Service.MessageDelivery;

import com.example.Entitiy.MessageDelivery;

public interface MessageDeliveryService {
    MessageDelivery create(MessageDelivery delivery);
    void delete(MessageDelivery delivery);
    MessageDelivery getById(String receiverId);
}