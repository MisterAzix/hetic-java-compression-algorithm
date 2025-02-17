package org.hetic.domain;

import org.hetic.domain.model.Chunk;
import org.hetic.domain.repository.CompressionRepository;
import org.hetic.domain.strategy.CompressionStrategy;
import org.hetic.domain.strategy.ChunkingStrategy;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class CompressionService {
    private final CompressionStrategy compressionStrategy;
    private final CompressionRepository compressionRepository;
    private final ChunkingStrategy chunkingStrategy;

    public CompressionService(
            CompressionStrategy compressionStrategy,
            CompressionRepository compressionRepository,
            ChunkingStrategy chunkingStrategy) {
        this.compressionStrategy = compressionStrategy;
        this.compressionRepository = compressionRepository;
        this.chunkingStrategy = chunkingStrategy;
    }

    public void processWholeFile(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        byte[] compressedFile = compressionStrategy.compress(fileContent);
        compressionRepository.storeCompression(compressedFile);
    }

    public void processChunks(File file) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            List<Chunk> chunks = chunkingStrategy.chunk(inputStream);

            for (Chunk chunk : chunks) {
                byte[] compressedBytes = compressionStrategy.compress(chunk.getContent());
                compressionRepository.storeCompression(compressedBytes);
            }
        }
    }
}