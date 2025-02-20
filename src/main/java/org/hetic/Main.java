package org.hetic;

import org.hetic.adapters.repository.InMemoryChunkRepository;
import org.hetic.adapters.strategy.RabinChunkingStrategy;
import org.hetic.adapters.strategy.SHA256HashingStrategy;
import org.hetic.domain.ChunkingService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/file.txt");

        byte[] content = new byte[10240];
        Arrays.fill(content, (byte) 1);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }

        InMemoryChunkRepository inMemoryChunkRepository = new InMemoryChunkRepository();
        RabinChunkingStrategy rabinChunkingStrategy = new RabinChunkingStrategy();
        SHA256HashingStrategy hashingStrategy = new SHA256HashingStrategy();

        ChunkingService inMemoryService = new ChunkingService(
            inMemoryChunkRepository, 
            rabinChunkingStrategy,
            hashingStrategy
        );

        Instant start = Instant.now();
        inMemoryService.processFile(file);
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");

        inMemoryChunkRepository.getStorage().forEach(chunk -> System.out.println(chunk.length));
    }
}