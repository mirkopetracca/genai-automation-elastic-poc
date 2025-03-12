package com.demo.ai.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)  // Change to your ES server
        ).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}
