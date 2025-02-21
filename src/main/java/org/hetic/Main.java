package org.hetic;

import org.hetic.adapters.inMemory.InMemoryChunkRepository;
import org.hetic.adapters.inMemory.InMemoryFileRepository;
import org.hetic.adapters.rabin.RabinChunkingStrategy;
import org.hetic.adapters.sha256.SHA256HashingStrategy;
import org.hetic.adapters.zstd.ZstdCompressionStrategy;
import org.hetic.domain.ChunkingService;
import org.hetic.domain.CompressionFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/test_image_duplication.png");

        InMemoryChunkRepository inMemoryChunkRepository = new InMemoryChunkRepository();
        InMemoryFileRepository inMemoryFileRepository = new InMemoryFileRepository();
        RabinChunkingStrategy rabinChunkingStrategy = new RabinChunkingStrategy();
        SHA256HashingStrategy hashingStrategy = new SHA256HashingStrategy();
        CompressionFactory compressionFactory = new CompressionFactory();

        ChunkingService inMemoryService = new ChunkingService(
                inMemoryChunkRepository,
                inMemoryFileRepository,
                rabinChunkingStrategy,
                hashingStrategy,
                compressionFactory
        );

        Instant start = Instant.now();
        inMemoryService.processFile(file);
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");
    }
}