package com.example.Controller;

import com.example.Entitiy.GroupRequestMembers;
import com.example.Entitiy.GroupRequestMembersId;
import com.example.Service.GroupRequestMembers.GroupRequestMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GroupRequestMembersController {

    private final GroupRequestMembersService groupRequestMembersService;

    @Autowired
    public GroupRequestMembersController(GroupRequestMembersService groupRequestMembersService) {
        this.groupRequestMembersService = groupRequestMembersService;
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
        return groupRequestMembersService.updateMember(member);
    }

    public static class UpdateStatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}