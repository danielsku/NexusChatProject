package com.example.Service.MessageDelivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.DAO.MessageDeliveryRepository;
import com.example.Entitiy.MessageDelivery;
import java.util.Optional;

@Service
public class MessageDeliveryServiceImpl implements MessageDeliveryService {

    private final MessageDeliveryRepository messageDeliveryRepository;

    @Autowired
    public MessageDeliveryServiceImpl(MessageDeliveryRepository messageDeliveryRepository) {
        this.messageDeliveryRepository = messageDeliveryRepository;
    }

    @Override
    public MessageDelivery create(MessageDelivery delivery) {
        return messageDeliveryRepository.save(delivery);
    }

    @Override
    public void delete(MessageDelivery delivery) {
        messageDeliveryRepository.delete(delivery);
    }

    @Override
    public MessageDelivery getById(String receiverId) {
        return messageDeliveryRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Did not find receiver id - " + receiverId));
    }
}