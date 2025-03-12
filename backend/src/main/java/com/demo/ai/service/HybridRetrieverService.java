package com.demo.ai.service;

import com.demo.ai.model.DocumentChunkEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HybridRetrieverService {

    private final ElasticSearchService elasticSearchService;
    private final FaissVectorService faissVectorService;

    public HybridRetrieverService(ElasticSearchService elasticSearchService, FaissVectorService faissVectorService) {
        this.elasticSearchService = elasticSearchService;
        this.faissVectorService = faissVectorService;
    }

    public List<DocumentChunkEntity> retrieveRelevantChunks(String query, List<Float> queryEmbedding) {
        // 1️⃣ Retrieve using BM25 (Elasticsearch)
        List<DocumentChunkEntity> bm25Results = elasticSearchService.search(query);

        // 2️⃣ Retrieve using FAISS (Vector similarity search)
        List<DocumentChunkEntity> vectorResults = faissVectorService.search(queryEmbedding);

        // 3️⃣ Merge and rank results intelligently
        return mergeAndRankResults(bm25Results, vectorResults);
    }

    private List<DocumentChunkEntity> mergeAndRankResults(
            List<DocumentChunkEntity> bm25Results, List<DocumentChunkEntity> vectorResults) {
        // 🔹 Combine results, giving priority to common documents
        return bm25Results.stream()
                .filter(vectorResults::contains)  // Keep documents that appear in both searches
                .collect(Collectors.toList());
    }
}