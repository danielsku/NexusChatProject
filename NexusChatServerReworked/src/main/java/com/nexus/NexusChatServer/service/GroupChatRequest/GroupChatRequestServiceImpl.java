package com.nexus.NexusChatServer.service.GroupChatRequest;

import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.repository.GroupChatRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<GroupChatRequest> existRequests = repo.findById(request.getRequestId());
        if (existRequests.isEmpty()) {
            throw new RuntimeException("Group chat request not found with id " + request.getRequestId());
        }
        GroupChatRequest exRequest = existRequests.get();

        exRequest.setSenderId(request.getSenderId()); // fixed
        exRequest.setChatName(request.getChatName());
        exRequest.setCreatedAt(request.getCreatedAt());

        return repo.save(exRequest);
    }

    @Override
    public void deleteRequest(GroupChatRequest request) {
        repo.delete(request);
    }


    @Override
    public List<GroupChatRequest> getRequestsBySenderId(String sender_id) {
        List<GroupChatRequest> allRequests = repo.findAll();
        return allRequests.stream().filter(request -> request.getSenderId().equals(sender_id))
                .collect(Collectors.toList());
    }

    // This should return a singular GroupRequest
//    @Override
//    public List<GroupChatRequest> getGroupChatRequestById(String requestId) {
//        List<GroupChatRequest> allRequests = repo.findAll();
//        return allRequests.stream().filter(request -> request.getRequest_id().equals(requestId))
//                .collect(Collectors.toList());
//    }

    @Override
    public GroupChatRequest getGroupChatRequestById(String requestId) {
        Optional<GroupChatRequest> result =  repo.findById(requestId);

        if(result.isPresent()){
            System.out.println("GroupRequest does not exist!");
        }

        return result.get();
    }
}
