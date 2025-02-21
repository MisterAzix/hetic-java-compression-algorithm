package org.hetic;

import org.hetic.adapters.inMemory.InMemoryChunkRepository;
import org.hetic.adapters.inMemory.InMemoryFileRepository;
import org.hetic.adapters.rabin.RabinChunkingStrategy;
import org.hetic.adapters.sha256.SHA256HashingStrategy;
import org.hetic.config.AppConfig;
import org.hetic.domain.ChunkingService;
import org.hetic.domain.CompressionFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        printSplashScreen();
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

        long initialSize = file.length();
        System.out.println("Start processing file...\n");
        System.out.printf("File: %s (%d bytes)\n", file.getName(), initialSize);
        System.out.printf("Compression: %s\n", AppConfig.isCompressionEnabled());
        System.out.printf("Algorithm: %s\n\n", AppConfig.getCompressionAlgorithm());
        Instant start = Instant.now();
        inMemoryService.processFile(file);
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");

        Map<String, byte[]> chunks = inMemoryChunkRepository.getAllChunks();
        int finalSize = chunks.values().stream().mapToInt(chunk -> chunk.length).sum();
        double compressionPercentage = ((double) (initialSize - finalSize) / initialSize) * 100;
        System.out.printf("Initial size: %d bytes, Final size: %d bytes, Compression: %.2f%%\n", initialSize, finalSize, compressionPercentage);
    }

    private static void printSplashScreen() {
        System.out.println("==================================================");
        System.out.println("=                                                =");
        System.out.println("=               HETIC CDC Algorithm              =");
        System.out.println("=                                                =");
        System.out.println("==================================================");
    }
}