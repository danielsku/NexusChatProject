package com.nexus.nexuschat.http;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.nexuschat.SQLitedatabase.service.IdentityService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

// TODO: Add authentication to REST API server and Clients
public class NexusClient {
    private final IdentityService identityService;
    private final String baseUrl;
    private final HttpClient client;
    private final ObjectMapper objectMapper;


    public NexusClient(IdentityService identityService, String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.identityService = identityService;
    }

    public String buildUrl(String path) {
        return baseUrl + path;
    }

    protected HttpRequest buildRequest(String path, HttpMethod method, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl(path)))
                .header("Content-Type", "application/json");

        switch (method) {
            case POST -> builder.POST(HttpRequest.BodyPublishers.ofString(body));
            case PUT -> builder.PUT(HttpRequest.BodyPublishers.ofString(body));
            case GET -> builder.GET();
            case DELETE -> builder.DELETE();
        }

        return builder.build();
    }

    protected HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected <T> T sendAndParse(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = send(request);

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
        }

        return getObjectMapper().readValue(response.body(), responseType);
    }

    protected <T> List<T> sendAndParseList(HttpRequest request, Class<T> elementType) throws IOException, InterruptedException {
        String responseBody = send(request).body();

        JavaType type = getObjectMapper()
                .getTypeFactory()
                .constructCollectionType(List.class, elementType);

        return getObjectMapper().readValue(responseBody, type);
    }

    protected IdentityService getIdentityService() {
        return identityService;
    }

    protected HttpClient getClient() {
        return client;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getUserId() {
        return getIdentityService().readIdentity().getUserId();
    }
}

// "http://localhost:8080";
