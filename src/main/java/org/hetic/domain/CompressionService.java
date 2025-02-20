package org.hetic.domain;

import org.hetic.domain.model.Chunk;
import org.hetic.domain.repository.ChunkRepository;
import org.hetic.domain.strategy.CompressionStrategy;
import org.hetic.domain.strategy.ChunkingStrategy;
import org.hetic.domain.strategy.HashingStrategy;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class CompressionService {
    private final CompressionStrategy compressionStrategy;
    private final ChunkRepository chunkRepository;
    private final ChunkingStrategy chunkingStrategy;
    private final HashingStrategy hashingStrategy;

    public CompressionService(
            CompressionStrategy compressionStrategy,
            ChunkRepository chunkRepository,
            ChunkingStrategy chunkingStrategy,
            HashingStrategy hashingStrategy) {
        this.compressionStrategy = compressionStrategy;
        this.chunkRepository = chunkRepository;
        this.chunkingStrategy = chunkingStrategy;
        this.hashingStrategy = hashingStrategy;
    }

    public void processWholeFile(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        byte[] compressedFile = compressionStrategy.compress(fileContent);
        String hash = hashingStrategy.hash(compressedFile);
        chunkRepository.storeChunkWithHash(hash, compressedFile);
    }

    public void processChunks(File file) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            List<Chunk> chunks = chunkingStrategy.chunk(inputStream);

            for (Chunk chunk : chunks) {
                byte[] compressedBytes = compressionStrategy.compress(chunk.getContent());
                String hash = hashingStrategy.hash(compressedBytes);
                chunkRepository.storeChunkWithHash(hash, compressedBytes);
            }
        }
    }
}