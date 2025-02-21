package org.hetic.domain;

import org.hetic.adapters.inMemory.InMemoryChunkRepository;
import org.hetic.adapters.inMemory.InMemoryFileRepository;
import org.hetic.adapters.rabin.RabinChunkingStrategy;
import org.hetic.adapters.sha256.SHA256HashingStrategy;
import org.hetic.adapters.zstd.ZstdCompressionStrategy;
import org.hetic.domain.model.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChunkingServiceTest {
    RabinChunkingStrategy rabinChunkingStrategy;
    private ChunkingService chunkingService;
    private InMemoryChunkRepository chunkRepository;
    private InMemoryFileRepository fileRepository;

    @BeforeEach
    void setUp() {
        chunkRepository = new InMemoryChunkRepository();
        fileRepository = new InMemoryFileRepository();
        rabinChunkingStrategy = new RabinChunkingStrategy();
        SHA256HashingStrategy hashingStrategy = new SHA256HashingStrategy();
        ZstdCompressionStrategy compressionStrategy = new ZstdCompressionStrategy();
        chunkingService = new ChunkingService(chunkRepository, fileRepository, rabinChunkingStrategy, hashingStrategy, compressionStrategy);
    }

    @Test
    void should_produce_expected_chunk_sizes() throws IOException {
        // Given
        File file = new File("src/test/resources/test.txt");
        int expectedChunksLength = 290;

        // When
        chunkingService.processFile(file);

        // Then
        Map<String, byte[]> chunks = chunkRepository.getAllChunks();
        assertEquals(expectedChunksLength, chunks.size(), "Number of chunks should match expected value");
    }

    @Test
    void should_store_only_unique_chunks() throws IOException {
        //Given
        File file = new File("src/test/resources/file_with_duplication.txt");
        int expectedChunksLength = 137;
        int expectedStoredChunksLength = 129;

        // When
        chunkingService.processFile(file);

        // Then
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        List<Chunk> chunks = rabinChunkingStrategy.chunk(inputStream);
        Map<String, byte[]> storedChunks = chunkRepository.getAllChunks();

        assertEquals(expectedChunksLength, chunks.size(), "Number of stored chunks should match expected value");
        assertEquals(expectedStoredChunksLength, storedChunks.size(), "Number of stored chunks should match expected value");
        assertTrue(storedChunks.size() < chunks.size(), "Number of stored chunks should be less than the number of chunks in the file");
    }

    @Test
    void should_handle_empty_file() throws IOException {
        File tempFile = File.createTempFile("empty_test", ".txt");
        tempFile.deleteOnExit();

        chunkingService.processFile(tempFile);

        Map<String, byte[]> storedChunks = chunkRepository.getAllChunks();
        assertTrue(storedChunks.isEmpty(), "Storage should be empty for an empty file");
    }
}