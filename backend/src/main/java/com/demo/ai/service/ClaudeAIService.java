package com.demo.ai.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaudeAIService {
    private static final String apiUrl = "https://api.anthropic.com/v1/messages";

    @Value("${anthropic.api.key}")
    private String apiKey;

    public List<String> filterChunksWithReAG(List<String> retrievedChunks, String query) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", "application/json");

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "claude-2");
            requestBody.put("query", query);
            requestBody.put("chunks", retrievedChunks);

            request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONObject jsonObject = new JSONObject(jsonResponse);
                return jsonObject.getJSONArray("filtered_chunks").toList().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling ClaudeAI API", e);
        }
    }

    public String generateCodeFromChunks(List<String> filteredChunks, String query) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", "application/json");

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "claude-2");
            requestBody.put("query", "Generate a function based on: " + query);
            requestBody.put("chunks", filteredChunks);

            request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling ClaudeAI API for code generation", e);
        }
    }
}
