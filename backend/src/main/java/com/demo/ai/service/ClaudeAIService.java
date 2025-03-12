package com.demo.ai.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClaudeAIService {
    private static final String apiUrl = "https://api.anthropic.com/v1/messages";

    @Value("${anthropic.api.key}")
    private String anthropicKey;

    @Value("${anthropic.model}")
    private String anthropicModel;

    public List<String> filterChunksWithReAG(String retrievedChunks, String query) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            //request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", MediaType.APPLICATION_JSON);
            request.setHeader("x-api-key", anthropicKey);
            request.setHeader("anthropic-version", "2023-06-01");

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", anthropicModel);
            requestBody.put("max_tokens", 8192);
            //requestBody.put("query", query);
            //requestBody.put("chunks", retrievedChunks);

            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", query),
                    Map.of("role", "user", "content", retrievedChunks)
            ));

            request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

            System.out.println(requestBody.toString());

            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.println("######## jsonResponse: "+jsonResponse);
                JSONObject jsonObject = new JSONObject(jsonResponse);
                return jsonObject.getJSONArray("content").toList().stream()
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
            //request.setHeader("Authorization", "Bearer " + apiKey);
            request.setHeader("Content-Type", MediaType.APPLICATION_JSON);
            request.setHeader("x-api-key", anthropicKey);
            request.setHeader("anthropic-version", "2023-06-01");

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
