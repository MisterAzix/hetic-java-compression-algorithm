package org.hetic.domain;

import org.hetic.adapters.repository.InMemoryChunkRepository;
import org.hetic.adapters.strategy.RabinChunkingStrategy;
import org.hetic.adapters.strategy.SHA256HashingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicationTest {
    private ChunkingService chunkingService;
    private InMemoryChunkRepository repository;
    private SHA256HashingStrategy hashingStrategy;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChunkRepository();
        RabinChunkingStrategy rabinStrategy = new RabinChunkingStrategy();
        hashingStrategy = new SHA256HashingStrategy();
        chunkingService = new ChunkingService(repository, rabinStrategy, hashingStrategy);
    }

    @Test
    void should_detect_duplicate_chunks() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        byte[] duplicateContent = new byte[4096]; // 4KB
        Arrays.fill(duplicateContent, (byte) 1);
        byte[] fullContent = Arrays.copyOf(duplicateContent, duplicateContent.length * 2);
        System.arraycopy(duplicateContent, 0, fullContent, duplicateContent.length, duplicateContent.length);
        Files.write(tempFile.toPath(), fullContent);

        chunkingService.processFile(tempFile);

        assertTrue(repository.getStorage().size() < fullContent.length / 2048,
                "Number of stored chunks should be reduced due to deduplication");
    }
}