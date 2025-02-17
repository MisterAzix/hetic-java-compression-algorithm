package org.hetic.domain;

import org.hetic.domain.repository.DecompressionRepository;
import org.hetic.domain.strategy.DecompressionStrategy;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class DecompressionService {
    private final DecompressionStrategy decompressionStrategy;
    private final DecompressionRepository decompressionRepository;

    public DecompressionService(
            DecompressionStrategy decompressionStrategy,
            DecompressionRepository decompressionRepository) {
        this.decompressionStrategy = decompressionStrategy;
        this.decompressionRepository = decompressionRepository;
    }

    public void processWholeFile(File compressedFile) throws IOException {
        byte[] compressedContent = Files.readAllBytes(compressedFile.toPath());
        byte[] decompressedFile = decompressionStrategy.decompress(compressedContent);
        decompressionRepository.storeDecompression(decompressedFile);
    }

    public void processChunks(List<File> compressedChunks) throws IOException {
        for (File chunk : compressedChunks) {
            byte[] compressedContent = Files.readAllBytes(chunk.toPath());
            byte[] decompressedChunk = decompressionStrategy.decompress(compressedContent);
            decompressionRepository.storeDecompression(decompressedChunk);
        }
    }
}