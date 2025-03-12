package com.demo.ai.task;
	
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.demo.ai.service.SmartChunkingService;

@Component
public class SmartChunking {

    private final SmartChunkingService smartChunkingService;
    
    public SmartChunking(SmartChunkingService smartChunkingService) {
        this.smartChunkingService = smartChunkingService;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleCreateChunks() {
        smartChunkingService.createChunks();
    }
}


