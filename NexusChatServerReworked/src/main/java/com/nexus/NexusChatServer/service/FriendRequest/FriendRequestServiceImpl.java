package com.nexus.NexusChatServer.service.FriendRequest;

import com.nexus.NexusChatServer.entity.FriendRequest;
import com.nexus.NexusChatServer.repository.FriendRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {
    private FriendRequestRepository repo;

    @Autowired
    public FriendRequestServiceImpl(FriendRequestRepository frepo) {
        this.repo = frepo;
    }

    @Override
    public FriendRequest createFriendRequest(FriendRequest request) {
        System.out.println("Saved friend request " + request);
        return repo.save(request);
    }

    @Override
    public FriendRequest updateFriendRequest(FriendRequest request) {
        Optional<FriendRequest> existRequests = repo.findById(request.getRequestId());
        if (existRequests.isEmpty()) {
            throw new RuntimeException("Friend request not found with id " + request.getRequestId());
        }
        FriendRequest exRequest = existRequests.get();

        exRequest.setSenderId(request.getSenderId()); // fixed
        exRequest.setReceiverId(request.getReceiverId());
        exRequest.setCreatedAt(request.getCreatedAt());
        exRequest.setStat(request.getStat()); // or exRequest.setStatus(request.getStatus());

        return repo.save(exRequest);
    }

    @Override
    public void deleteFriendRequest(FriendRequest request) {
        repo.delete(request);
    }

    @Override
    public List<FriendRequest> getRequestsByReceiverId(String receiver_id) {
        List<FriendRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getReceiverId().equals(receiver_id))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> getRequestsBySenderId(String sender_id) {
        List<FriendRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getSenderId().equals(sender_id))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> getAllRequests() {
        return repo.findAll();
    }

    @Override
    public FriendRequest getFriendRequestById(String id) {
        Optional<FriendRequest> result = repo.findById(id);
        FriendRequest theFriendRequest =  null;
        if(result.isPresent()){
            theFriendRequest = result.get();
        }
        return theFriendRequest;
    }
}
