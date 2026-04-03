package com.example.Service.GroupRequestMembers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.DAO.GroupRequestMemberRepository;
import com.example.Entitiy.*;
import com.example.Entitiy.GroupRequestMembers;
import com.example.Entitiy.GroupRequestMembersId;
import org.springframework.stereotype.Service;

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
                member.getReceiverId());

        Optional<GroupRequestMembers> existMembers = repo.findById(id);

        if (existMembers.isEmpty()) {
            throw new RuntimeException("Member not found");
        }

        GroupRequestMembers exMember = existMembers.get();
        exMember.setStat(member.getStat());

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
}
