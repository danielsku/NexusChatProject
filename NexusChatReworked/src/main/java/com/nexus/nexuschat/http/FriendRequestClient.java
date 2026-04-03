package com.nexus.nexuschat.http;


import com.nexus.nexuschat.SQLitedatabase.model.FriendRequest;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FriendRequestClient extends NexusClient{

    private static final String PATH = "/api/friend-requests";

    public FriendRequestClient(IdentityService identityService, String baseUrl) {
        super(identityService, baseUrl);
    }

    public String getFriendRequestUrl() {
        return buildUrl(PATH);
    }

    public FriendRequest create(FriendRequest friendRequest) throws IOException, InterruptedException {
        String body = getObjectMapper().writeValueAsString(friendRequest);

        HttpRequest request = buildRequest(PATH, HttpMethod.POST, body);

        return sendAndParse(request, FriendRequest.class);
    }

    public FriendRequest update(FriendRequest friendRequest) throws IOException, InterruptedException {
        String body = getObjectMapper().writeValueAsString(friendRequest);

        HttpRequest request = buildRequest(PATH + "/" + friendRequest.getRequestId(), HttpMethod.PUT, body);
        System.out.println("Sent request for updating : " + friendRequest);
        return sendAndParse(request, FriendRequest.class);
    }

    public void delete(String requestId) throws IOException, InterruptedException {
        HttpRequest request = buildRequest(PATH + "/" + requestId, HttpMethod.DELETE, null);

        HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete friend request: "
                    + response.statusCode() + " - " + response.body());
        }
    }
}
