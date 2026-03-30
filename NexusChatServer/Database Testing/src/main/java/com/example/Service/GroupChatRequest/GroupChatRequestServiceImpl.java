package com.example.Service.GroupChatRequest;

import com.example.DAO.*;
import com.example.Entitiy.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

@Service
public class GroupChatRequestServiceImpl implements GroupChatRequestService {
    private GroupChatRequestRepository repo;

    @Autowired
    public GroupChatRequestServiceImpl(GroupChatRequestRepository repo) {
        this.repo = repo;
    }

    @Override
    public GroupChatRequest createRequest(GroupChatRequest request) {
        return repo.save(request);
    }

    @Override
    public GroupChatRequest updateRequest(GroupChatRequest request) {
        Optional<GroupChatRequest> existRequests = repo.findById(request.getId());
        GroupChatRequest exRequest = existRequests.get();

        exRequest.setId(request.getId());
        exRequest.setSenderId(request.getId());
        exRequest.setReceiver_id(request.getReceiver_id());
        exRequest.setCreated_at(request.getCreated_at());
        exRequest.setStat(request.getStat());

        return repo.save(exRequest);
    }

    @Override
    public void deleteRequest(GroupChatRequest request) {
        repo.delete(request);
    }

    @Override
    public List<GroupChatRequest> getRequestsByReceiverId(String receiver_id) {
        List<GroupChatRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getReceiver_id().equals(receiver_id))
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupChatRequest> getRequestsBySenderId(String sender_id) {
        List<GroupChatRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getSenderId().equals(sender_id))
                .collect(Collectors.toList());
    }
}
