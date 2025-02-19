package org.hetic.domain;

import org.hetic.adapters.repository.InMemoryChunkRepository;
import org.hetic.adapters.strategy.RabinChunkingStrategy;
import org.hetic.adapters.strategy.SHA256HashingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;




class ChunkingServiceTest {
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
    void should_produce_expected_chunk_sizes() throws IOException {
        File file = new File("src/test/resources/test.txt");
        byte[] content = new byte[52613];
        Arrays.fill(content, (byte) 1);
    
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }
    
        chunkingService.processFile(file);
    
        List<byte[]> chunks = inMemoryChunkRepository.getStorage();
        System.out.println("Actual chunk sizes:");
        chunks.forEach(chunk -> System.out.println(chunk.length));
    

        assertEquals(content.length, chunks.stream().mapToInt(chunk -> chunk.length).sum(),
                "Total size should match input size");
        chunks.forEach(chunk -> {
            assertTrue(chunk.length >= 2048, "Chunk size should be >= 2KB");
            assertTrue(chunk.length <= 16384, "Chunk size should be <= 16KB");
        });
    }

    @Test
    void should_deduplicate_identical_chunks() throws IOException {
        File tempFile = File.createTempFile("dedup_test", ".txt");
        tempFile.deleteOnExit();
        
        byte[] chunk = new byte[4096];
        Arrays.fill(chunk, (byte) 1);
        
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            for (int i = 0; i < 4; i++) {
                fos.write(chunk);
            }
        }

        chunkingService.processFile(tempFile);

        List<byte[]> storedChunks = inMemoryChunkRepository.getStorage();
        
        Set<String> uniqueHashes = storedChunks.stream()
            .map(c -> new SHA256HashingStrategy().hash(c))
            .collect(Collectors.toSet());

        assertTrue(uniqueHashes.size() < 4, "Number of unique chunks should be less than number of chunks written");
        assertTrue(storedChunks.size() < 4, "Storage should contain fewer chunks than written due to deduplication");
    }

    @Test
    void should_handle_empty_file() throws IOException {
        File tempFile = File.createTempFile("empty_test", ".txt");
        tempFile.deleteOnExit();

        chunkingService.processFile(tempFile);

        List<byte[]> storedChunks = inMemoryChunkRepository.getStorage();
        assertTrue(storedChunks.isEmpty(), "Storage should be empty for an empty file");
    }
    @Test
    void should_handle_large_file() throws IOException {
        File tempFile = File.createTempFile("large_test", ".txt");
        tempFile.deleteOnExit();
    
        byte[] largeContent = new byte[10 * 1024 * 1024];  // 10MB
        Arrays.fill(largeContent, (byte) 1);
    
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(largeContent);
        }
    
        chunkingService.processFile(tempFile);
    
        List<byte[]> storedChunks = inMemoryChunkRepository.getStorage();
        int totalSize = storedChunks.stream().mapToInt(chunk -> chunk.length).sum();
    
        // Debug prints
        System.out.println("Original size: " + largeContent.length);
        System.out.println("Total chunks size: " + totalSize);
        System.out.println("Number of chunks: " + storedChunks.size());
        System.out.println("Individual chunk sizes:");
        storedChunks.forEach(chunk -> System.out.println(chunk.length));
    
        assertEquals(largeContent.length, totalSize, "Total size of chunks should match the size of the large file");
        assertTrue(storedChunks.size() < largeContent.length / 2048, 
            "Number of chunks should be less than maximum possible chunks due to deduplication");
    }

    @Test
    void should_handle_file_with_varied_content() throws IOException {
        File tempFile = File.createTempFile("varied_test", ".txt");
        tempFile.deleteOnExit();

        byte[] variedContent = new byte[8192];
        for (int i = 0; i < variedContent.length; i++) {
            variedContent[i] = (byte) (i % 256);
        }

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(variedContent);
        }

        chunkingService.processFile(tempFile);

        List<byte[]> storedChunks = inMemoryChunkRepository.getStorage();
        int totalSize = storedChunks.stream().mapToInt(chunk -> chunk.length).sum();

        assertEquals(variedContent.length, totalSize, "Total size of chunks should match the size of the varied content file");
    }
}