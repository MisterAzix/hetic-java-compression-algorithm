package org.hetic.domain;

import org.hetic.domain.model.Chunk;
import org.hetic.domain.repository.ChunkRepository;
import org.hetic.domain.strategy.ChunkingStrategy;
import org.hetic.domain.strategy.HashingStrategy;

import java.io.*;
import java.util.List;

public class ChunkingService {
    private final ChunkRepository chunkRepository;
    private final ChunkingStrategy chunkingStrategy;
    private final HashingStrategy hashingStrategy;

    public ChunkingService(ChunkRepository chunkRepository, 
                          ChunkingStrategy chunkingStrategy,
                          HashingStrategy hashingStrategy) {
        this.chunkRepository = chunkRepository;
        this.chunkingStrategy = chunkingStrategy;
        this.hashingStrategy = hashingStrategy;
    }

    public void processFile(File file) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            List<Chunk> chunks = chunkingStrategy.chunk(inputStream);
            
            for (Chunk chunk : chunks) {
                byte[] content = chunk.getContent();
                String hash = hashingStrategy.hash(content);
                chunkRepository.storeChunkWithHash(content, hash);
            }
        }
    }
}