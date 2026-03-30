package com.nexus.nexuschat.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;
import com.nexus.nexuschat.pojo.SyncResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SyncClient {

    private IdentityService identityService;
    private static String BASE_URL;
    private final HttpClient client;
    private final ObjectMapper objectMapper;


    public SyncClient(IdentityService identityService) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.identityService = identityService;
        BASE_URL = BaseURL.getURL() + "/api/sync/" + identityService.readIdentity().getUserId();
    }

    public SyncResponse findAllSyncInformation() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to sync: " + response.statusCode() + " - " + response.body());
        }

        return objectMapper.readValue(response.body(), SyncResponse.class);
    }

}
