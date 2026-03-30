package com.nexus.NexusChatServer.service.GroupRequestMembers;

import com.nexus.NexusChatServer.entity.GroupRequestMembers;
import com.nexus.NexusChatServer.entity.GroupRequestMembersId;
import com.nexus.NexusChatServer.repository.GroupRequestMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupRequestMembersServiceImpl implements GroupRequestMembersService {
    private GroupRequestMemberRepository repo;

    @Autowired
    public GroupRequestMembersServiceImpl(GroupRequestMemberRepository repo) {
        this.repo = repo;
    }

    @Override
    public GroupRequestMembers createMember(GroupRequestMembers member) {
        return repo.save(member);
    }

    @Override
    public GroupRequestMembers updateMember(GroupRequestMembers member) {
        GroupRequestMembersId id = new GroupRequestMembersId(
                member.getRequestId(),
                member.getReceiverId()
        );

        Optional<GroupRequestMembers> existMembers = repo.findById(id);
        if (existMembers.isEmpty()) {
            throw new RuntimeException("Member not found for request " + member.getRequestId() + " and receiver " + member.getReceiverId());
        }

        GroupRequestMembers exMember = existMembers.get();
        exMember.setStat(member.getStat());
        exMember.setUsername(member.getUsername()); // optional: update username if needed

        return repo.save(exMember);
    }

    @Override
    public void deleteMember(GroupRequestMembers member) {
        repo.delete(member);
    }

    @Override
    public List<GroupRequestMembers> getMembersByRequestId(String request_id) {
        List<GroupRequestMembers> allMembers = repo.findAll();
        return allMembers.stream().filter(member -> member.getRequest().getId().equals(request_id))
                .collect(Collectors.toList());
    }

    // TODO : Check over this in the afternoon
    @Override
    public List<GroupRequestMembers> getRequestIdByMember(String receiverId) {
        List<GroupRequestMembers> allMembers = repo.findAll();
        return allMembers.stream().filter(member -> member.getReceiverId().equals(receiverId))
                .collect(Collectors.toList());
    }
}
