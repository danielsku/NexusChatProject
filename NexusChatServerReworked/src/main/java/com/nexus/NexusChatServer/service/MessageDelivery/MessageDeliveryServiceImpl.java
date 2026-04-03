package com.nexus.NexusChatServer.service.MessageDelivery;

import com.nexus.NexusChatServer.entity.MessageDelivery;
import com.nexus.NexusChatServer.repository.MessageDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Optional<MessageDelivery> result = messageDeliveryRepository.findById(receiverId);

        MessageDelivery theMessageDelivery = null;

        if(result.isPresent()){
            theMessageDelivery = result.get();
        } else {
            System.out.println("Did not find receiver id - " + receiverId);
        }

        return theMessageDelivery;
    }
}