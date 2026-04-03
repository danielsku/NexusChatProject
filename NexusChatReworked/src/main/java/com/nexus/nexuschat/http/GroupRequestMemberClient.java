package com.nexus.nexuschat.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexus.nexuschat.SQLitedatabase.model.GroupChat;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;
import com.nexus.nexuschat.pojo.GroupMember;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.List;

public class GroupRequestMemberClient extends NexusClient{

    private static final String PATH = "/api/group-request-members";

    public GroupRequestMemberClient(IdentityService identityService, String baseUrl) {
        super(identityService, baseUrl);
    }

    public String getGroupRequestMemberUrl() {
        return buildUrl(PATH);
    }

    public GroupMember update(GroupMember groupMember) throws IOException, InterruptedException {
        String body = getObjectMapper().writeValueAsString(groupMember);

        HttpRequest request = buildRequest(PATH + "/" + groupMember.getRequestId() + "/" + groupMember.getReceiverId(), HttpMethod.PUT, body);

        return sendAndParse(request, GroupMember.class);
    }

    public List<GroupMember> getAll(GroupRequest groupRequest) throws IOException, InterruptedException {
        String fullPath = PATH + "/" + groupRequest.getRequestId();

        HttpRequest request = buildRequest(fullPath, HttpMethod.GET, null);

        return sendAndParseList(request, GroupMember.class);
    }

}
