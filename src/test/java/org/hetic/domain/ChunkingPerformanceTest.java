package org.hetic.domain;

import org.hetic.adapters.repository.InMemoryChunkRepository;
import org.hetic.adapters.strategy.RabinChunkingStrategy;
import org.hetic.adapters.strategy.SHA256HashingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ChunkingPerformanceTest {
    private ChunkingService chunkingService;
    private InMemoryChunkRepository inMemoryChunkRepository;

    @BeforeEach
    void setUp() {
        inMemoryChunkRepository = new InMemoryChunkRepository();
        RabinChunkingStrategy rabinChunkingStrategy = new RabinChunkingStrategy();
        SHA256HashingStrategy hashingStrategy = new SHA256HashingStrategy();
        chunkingService = new ChunkingService(inMemoryChunkRepository, rabinChunkingStrategy, hashingStrategy);
    }

    @Test
    void should_measure_chunking_time() throws IOException {
        File file = new File("src/test/resources/test.txt");
        byte[] content = new byte[10240];
        Arrays.fill(content, (byte) 1);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }

        Instant start = Instant.now();
        chunkingService.processFile(file);
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");
        assertTrue(timeElapsed.toMillis() < 1000, "Chunking should be completed within 1 second");
    }
}