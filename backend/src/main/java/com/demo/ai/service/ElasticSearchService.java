package com.demo.ai.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.demo.ai.model.DocumentChunkEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<DocumentChunkEntity> search(String query) {
        List<DocumentChunkEntity> results = new ArrayList<>();

        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("documents_index")  // Your Elasticsearch index //era documents_index
                    .query(q -> q
                            .match(m -> m
                                    .field("chunkText")  // Field to search
                                    .query(query)
                            )
                    )
            );

            SearchResponse<DocumentChunkEntity> searchResponse =
                    elasticsearchClient.search(searchRequest, DocumentChunkEntity.class);

            for (Hit<DocumentChunkEntity> hit : searchResponse.hits().hits()) {
                results.add(hit.source());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error querying Elasticsearch", e);
        }

        return results;
    }
}