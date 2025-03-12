package com.demo.ai.service;

import com.demo.ai.model.DocumentChunkEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaissVectorService {

    private final FaissClient faissClient;

    public FaissVectorService(FaissClient faissClient) {
        this.faissClient = faissClient;
    }

    public List<DocumentChunkEntity> search(List<Float> queryEmbedding) {
        List<Long> matchingIds = faissClient.search(queryEmbedding);
        return matchingIds.stream()
                .map(id -> {
                    DocumentChunkEntity chunk = new DocumentChunkEntity();
                    chunk.setId(id);
                    return chunk;
                })
                .collect(Collectors.toList());
    }
}
