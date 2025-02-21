package org.hetic;

import org.hetic.adapters.inMemory.InMemoryChunkRepository;
import org.hetic.adapters.inMemory.InMemoryFileRepository;
import org.hetic.adapters.rabin.RabinChunkingStrategy;
import org.hetic.adapters.sha256.SHA256HashingStrategy;
import org.hetic.config.AppConfig;
import org.hetic.domain.ChunkingService;
import org.hetic.domain.factory.CompressionFactory;
import org.hetic.domain.factory.DecompressionFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class Main {
    static InMemoryChunkRepository inMemoryChunkRepository = new InMemoryChunkRepository();
    static InMemoryFileRepository inMemoryFileRepository = new InMemoryFileRepository();
    static RabinChunkingStrategy rabinChunkingStrategy = new RabinChunkingStrategy();
    static SHA256HashingStrategy hashingStrategy = new SHA256HashingStrategy();
    static CompressionFactory compressionFactory = new CompressionFactory();
    static DecompressionFactory decompressionFactory = new DecompressionFactory();

    public static void main(String[] args) throws IOException {
        printSplashScreen();
        File file = new File("src/main/resources/test_image_duplication.png");

        ChunkingService inMemoryService = new ChunkingService(
                inMemoryChunkRepository,
                inMemoryFileRepository,
                rabinChunkingStrategy,
                hashingStrategy,
                compressionFactory,
                decompressionFactory
        );

        String mode = AppConfig.getMode();
        System.out.printf("Mode: %s\n", mode);
        switch (mode) {
            case "chunking":
                chunkingMode(inMemoryService, file);
                break;
            case "whole":
                wholeFileMode(inMemoryService, file);
                break;
            case "build":
                buildFileMode(inMemoryService, "test_image_duplication.png");
                break;
            default:
                throw new IllegalArgumentException("Invalid processing mode!");
        }
    }

    private static void printSplashScreen() {
        System.out.println("==================================================");
        System.out.println("=                                                =");
        System.out.println("=               HETIC CDC Algorithm              =");
        System.out.println("=                                                =");
        System.out.println("==================================================");
    }

    private static void chunkingMode(ChunkingService inMemoryService, File file) throws IOException {
        long initialSize = file.length();
        System.out.println("Start processing file...\n");
        System.out.printf("File: %s (%d bytes)\n", file.getName(), initialSize);
        System.out.printf("Compression: %s\n", AppConfig.isCompressionEnabled() ? "Enabled" : "Disabled");
        System.out.printf("Algorithm: %s\n\n", AppConfig.getDecompressionAlgorithm());

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

    private static void wholeFileMode(ChunkingService inMemoryService, File file) throws IOException {
        long initialSize = file.length();
        System.out.println("Start processing file...\n");
        System.out.printf("File: %s (%d bytes)\n", file.getName(), initialSize);
        System.out.printf("Compression: %s\n", AppConfig.isCompressionEnabled() ? "Enabled" : "Disabled");
        System.out.printf("Algorithm: %s\n\n", AppConfig.getDecompressionAlgorithm());

        Instant start = Instant.now();
        inMemoryService.processWholeFile(file);
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");

        Map<String, byte[]> chunks = inMemoryChunkRepository.getAllChunks();
        int finalSize = chunks.values().stream().mapToInt(chunk -> chunk.length).sum();
        double compressionPercentage = ((double) (initialSize - finalSize) / initialSize) * 100;
        System.out.printf("Initial size: %d bytes, Final size: %d bytes, Compression: %.2f%%\n", initialSize, finalSize, compressionPercentage);
    }

    private static void buildFileMode(ChunkingService inMemoryService, String fileIdentifier) throws IOException {
        System.out.println("Start building file...\n");
        System.out.printf("File: %s\n", fileIdentifier);
        System.out.printf("Compression: %s\n", AppConfig.isCompressionEnabled() ? "Enabled" : "Disabled");
        System.out.printf("Algorithm: %s\n\n", AppConfig.getDecompressionAlgorithm());

        Instant start = Instant.now();
        inMemoryService.buildFile(fileIdentifier, new File("src/main/resources/output.png"));
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " ms");
    }
}