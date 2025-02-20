package org.hetic.domain;

import org.hetic.adapters.strategy.RabinChunkingStrategy;
import org.hetic.adapters.strategy.ZstdCompressionStrategy;
import org.hetic.domain.model.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompressionTest {
    private RabinChunkingStrategy chunkingStrategy;
    private ZstdCompressionStrategy compressionStrategy;

    @BeforeEach
    void setUp() {
        chunkingStrategy = new RabinChunkingStrategy();
        compressionStrategy = new ZstdCompressionStrategy();
    }

    @Test
    void should_compress_chunks() throws IOException {
        File file = new File("src/test/resources/file_with_duplication.txt");

        List<Chunk> chunks = chunkingStrategy.chunk(new BufferedInputStream(new FileInputStream(file)));
        int totalOriginalSize = 0;
        int totalCompressedSize = 0;
        for (byte[] chunk : chunks.stream().map(Chunk::getContent).toList()) {
            int originalSize = chunk.length;
            byte[] compressedChunk = compressionStrategy.compress(chunk);
            int compressedSize = compressedChunk.length;
            totalOriginalSize += originalSize;
            totalCompressedSize += compressedSize;
            System.out.println("Original size: " + originalSize + " bytes, Compressed size: " + compressedSize + " bytes");
        }
        System.out.println("Total original size: " + totalOriginalSize + " bytes, Total compressed size: " + totalCompressedSize + " bytes");
        assertTrue(totalCompressedSize < totalOriginalSize, "Total compressed size should be smaller than total original size");
    }


    @Test
    void should_compress_whole_file() throws IOException {
        File file = new File("src/test/resources/file_with_duplication.txt");

        List<Chunk> chunks = chunkingStrategy.chunk(new BufferedInputStream(new FileInputStream(file)));
        byte[] content = chunks.stream().map(Chunk::getContent).reduce(new byte[0], (a, b) -> {
            byte[] result = Arrays.copyOf(a, a.length + b.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        });

        int originalSize = content.length;
        byte[] compressedContent = compressionStrategy.compress(content);
        int compressedSize = compressedContent.length;
        System.out.println("Original size: " + originalSize + " bytes, Compressed size: " + compressedSize + " bytes");
        assertTrue(compressedContent.length < content.length, "Compressed content should be smaller than original content");
    }
}