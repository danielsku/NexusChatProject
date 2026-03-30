package com.nexus.NexusChatServer.controller.rest;

import com.nexus.NexusChatServer.dto.GroupMember;
import com.nexus.NexusChatServer.entity.GroupChatRequest;
import com.nexus.NexusChatServer.entity.GroupRequestMembers;
import com.nexus.NexusChatServer.entity.GroupRequestMembersId;
import com.nexus.NexusChatServer.service.GroupChatRequest.GroupChatRequestService;
import com.nexus.NexusChatServer.service.GroupRequestMembers.GroupRequestMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: For every Controller, use the DTO/POJO model instead of the JPA entity

@RestController
@RequestMapping("/api")
public class GroupRequestMembersController {

    private final GroupRequestMembersService groupRequestMembersService;
    private final GroupChatRequestService groupChatRequestService;

    @Autowired
    public GroupRequestMembersController(GroupRequestMembersService groupRequestMembersService, GroupChatRequestService groupChatRequestService) {
        this.groupRequestMembersService = groupRequestMembersService;
        this.groupChatRequestService = groupChatRequestService;
    }

    // PUT /group-request-members/{requestId}/{receiverId} → accept/decline
    @PutMapping("/group-request-members/{requestId}/{receiverId}")
    public GroupRequestMembers updateRequestMemberStatus(
            @PathVariable String requestId,
            @PathVariable String receiverId,
            @RequestBody UpdateStatusRequest statusRequest) {

        GroupRequestMembersId memberId = new GroupRequestMembersId(requestId, receiverId);
        GroupRequestMembers member = groupRequestMembersService.getMembersByRequestId(requestId).stream()
                .filter(m -> m.getReceiverId().equals(receiverId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group request member not found"));

        member.setStat(statusRequest.getStatus()); // "ACCEPTED" or "DECLINED"

        // If all groupChat members ACCEPTED/DECLINED,
        boolean flagForDeletion = true;
        for(GroupRequestMembers gm : groupRequestMembersService.getRequestIdByMember(requestId)){
            if(gm.getStat().equals("PENDING")){
                flagForDeletion = false;
                break;
            }
        }
        //remove all of them and group Request from MySQL database
        if(flagForDeletion){
            // Delete all GroupRequestMembers first
            for(GroupRequestMembers gm : groupRequestMembersService.getRequestIdByMember(requestId)){
                groupRequestMembersService.deleteMember(gm);
            }
            // Delete GroupRequest last
            GroupChatRequest request = groupChatRequestService.getGroupChatRequestById(requestId);

            if (request != null) {
                groupChatRequestService.deleteRequest(request);
            }
        }

        return groupRequestMembersService.updateMember(member);
    }

    public static class UpdateStatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // GET /group-request-members/{requestId} → get all members for a request
    @GetMapping("/group-request-members/{requestId}")
    public List<GroupMember> getAllMembersByRequestId(@PathVariable String requestId) {

        return groupRequestMembersService.getMembersByRequestId(requestId)
                .stream()
                .map(m -> new GroupMember(
                        m.getRequestId(),
                        m.getReceiverId(),
                        m.getUsername(),
                        m.getStat()
                ))
                .toList();
    }
}