package com.example.Service.FriendRequest;

import com.example.DAO.*;
import com.example.Entitiy.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {
    private FriendRequestRepository repo;

    @Autowired
    public FriendRequestServiceImpl(FriendRequestRepository frepo) {
        this.repo = frepo;
    }

    @Override
    public FriendRequest createFriendRequest(FriendRequest request) {
        return repo.save(request);
    }

    @Override
    public FriendRequest updateFriendRequest(FriendRequest request) {
        Optional<FriendRequest> existRequests = repo.findById(request.getId());
        FriendRequest exRequest = existRequests.get();

        exRequest.setId(request.getId());
        exRequest.setSender_id(request.getId());
        exRequest.setReceiver_id(request.getReceiver_id());
        exRequest.setCreated_at(request.getCreated_at());
        exRequest.setStatus(request.getStatus());

        return repo.save(exRequest);
    }

    @Override
    public void deleteFriendRequest(FriendRequest request) {
        repo.delete(request);
    }

    @Override
    public List<FriendRequest> getRequestsByReceiverId(String receiver_id) {
        List<FriendRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getReceiver_id().equals(receiver_id))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> getRequestsBySenderId(String sender_id) {
        List<FriendRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getSender_id().equals(sender_id))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> getAllRequests() {
        return repo.findAll();
    }
}
