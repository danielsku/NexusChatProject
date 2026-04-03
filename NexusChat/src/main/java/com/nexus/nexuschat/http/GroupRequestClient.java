package com.nexus.nexuschat.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexus.nexuschat.SQLitedatabase.model.GroupRequest;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;

import java.io.IOException;
import java.net.http.HttpRequest;

public class GroupRequestClient extends NexusClient{

    private static final String PATH = "/api/group-requests";

    public GroupRequestClient(IdentityService identityService, String baseUrl) {
        super(identityService, baseUrl);
    }

    public String getGroupRequestUrl() {
        return buildUrl(PATH);
    }

    public GroupRequest create(GroupRequest groupRequest) throws IOException, InterruptedException {
        String body = getObjectMapper().writeValueAsString(groupRequest);

        HttpRequest request = buildRequest(PATH, HttpMethod.POST, body);

        return sendAndParse(request, GroupRequest.class);
    }
}
