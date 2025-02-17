package org.hetic.domain;

import org.hetic.adapters.repository.InMemoryChunkRepository;
import org.hetic.adapters.strategy.RabinChunkingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompressionTest {
    private ChunkingService chunkingService;
    private InMemoryChunkRepository inMemoryChunkRepository;

    @BeforeEach
    void setUp() {
        inMemoryChunkRepository = new InMemoryChunkRepository();
        RabinChunkingStrategy rabinChunkingStrategy = new RabinChunkingStrategy();
        chunkingService = new ChunkingService(inMemoryChunkRepository, rabinChunkingStrategy);
    }

    @Test
    void should_compress_chunks() throws IOException {
        File file = new File("src/test/resources/test.txt");
        byte[] content = new byte[10240];
        Arrays.fill(content, (byte) 1);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }

        chunkingService.processFile(file);

        inMemoryChunkRepository.getStorage().forEach(chunk -> {
            int originalSize = chunk.length;
            byte[] compressedChunk = compress(chunk);
            int compressedSize = compressedChunk.length;
            System.out.println("Original size: " + originalSize + " bytes, Compressed size: " + compressedSize + " bytes");
            assertTrue(compressedSize < originalSize, "Compressed chunk should be smaller than original chunk");
        });
    }

    @Test
    void should_compare_compression_sizes() throws IOException {
        File file = new File("src/test/resources/test.txt");
        byte[] content = new byte[10240];
        Arrays.fill(content, (byte) 1);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }

        chunkingService.processFile(file);
        List<byte[]> chunks = inMemoryChunkRepository.getStorage();

        int totalCompressedChunkSize = chunks.stream()
                .mapToInt(chunk -> compress(chunk).length)
                .sum();

        byte[] globalCompressed = compress(content);
        int globalCompressedSize = globalCompressed.length;

        System.out.println("Total compressed chunk size: " + totalCompressedChunkSize + " bytes");
        System.out.println("Global compressed size: " + globalCompressedSize + " bytes");

        int tolerance = 100;
        assertTrue(Math.abs(globalCompressedSize - totalCompressedChunkSize) <= tolerance,
                "The total compressed chunk size should be approximately equal to the global compressed size");
    }

    private byte[] compress(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
             DeflaterOutputStream dos = new DeflaterOutputStream(bos, new Deflater())) {
            dos.write(data);
            dos.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress data", e);
        }
    }
}