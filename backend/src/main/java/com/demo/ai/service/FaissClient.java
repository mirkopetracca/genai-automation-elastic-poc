package com.demo.ai.service;

import com.demo.ai.model.DocumentChunkEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FaissClient {

    // Simulated FAISS index (Replace with actual FAISS JNI bindings if needed)
    private final List<float[]> vectorDatabase = new ArrayList<>();
    private final List<Long> documentIds = new ArrayList<>();

    public void addVector(Long id, float[] vector) {
        vectorDatabase.add(vector);
        documentIds.add(id);
    }

    public List<Long> search(List<Float> queryEmbedding) {
        List<Long> resultIds = new ArrayList<>();
        
        if (vectorDatabase.isEmpty()) {
            return resultIds; // No vectors in database
        }

        float[] queryVector = convertToPrimitiveArray(queryEmbedding);

        // Naive similarity search (Replace with proper FAISS search)
        for (int i = 0; i < vectorDatabase.size(); i++) {
            if (cosineSimilarity(queryVector, vectorDatabase.get(i)) > 0.8) { // Adjust threshold as needed
                resultIds.add(documentIds.get(i));
            }
        }

        return resultIds;
    }

    private float[] convertToPrimitiveArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private float cosineSimilarity(float[] vec1, float[] vec2) {
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }
        
        return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }
}
