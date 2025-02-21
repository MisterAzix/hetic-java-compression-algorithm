package org.hetic.domain;

import org.hetic.domain.factory.CompressionFactory;
import org.hetic.domain.factory.DecompressionFactory;
import org.hetic.domain.model.Chunk;
import org.hetic.domain.repository.ChunkRepository;
import org.hetic.domain.repository.FileRepository;
import org.hetic.domain.strategy.ChunkingStrategy;
import org.hetic.domain.strategy.CompressionStrategy;
import org.hetic.domain.strategy.DecompressionStrategy;
import org.hetic.domain.strategy.HashingStrategy;
import org.hetic.config.AppConfig;

import java.io.*;
import java.util.List;

public class ChunkingService {
    private final ChunkRepository chunkRepository;
    private final FileRepository fileRepository;
    private final ChunkingStrategy chunkingStrategy;
    private final HashingStrategy hashingStrategy;
    private final CompressionFactory compressionFactory;
    private final DecompressionFactory decompressionFactory;


    public ChunkingService(ChunkRepository chunkRepository,
                           FileRepository fileRepository,
                           ChunkingStrategy chunkingStrategy,
                           HashingStrategy hashingStrategy,
                           CompressionFactory compressionFactory, DecompressionFactory decompressionFactory) {
        this.chunkRepository = chunkRepository;
        this.fileRepository = fileRepository;
        this.chunkingStrategy = chunkingStrategy;
        this.hashingStrategy = hashingStrategy;
        this.compressionFactory = compressionFactory;
        this.decompressionFactory = decompressionFactory;
    }

    public void processFile(File file) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            List<Chunk> chunks = chunkingStrategy.chunk(inputStream);
            String fileIdentifier = file.getName();

            for (Chunk chunk : chunks) {
                byte[] content = chunk.getContent();
                if (AppConfig.isCompressionEnabled()) {
                    CompressionStrategy strategy = compressionFactory.getStrategy(AppConfig.getDecompressionAlgorithm());
                    content = strategy.compress(content);
                }
                String hash = hashingStrategy.hash(content);
                boolean isChunkDuplicate = chunkRepository.isChunkDuplicate(hash);
                if (!isChunkDuplicate) {
                    chunkRepository.storeChunkWithHash(hash, content);
                }
                fileRepository.addChunkToFile(fileIdentifier, hash);
            }
        }
    }

    public void processWholeFile(File file) throws IOException {
        String fileIdentifier = file.getName();
        byte[] content = getFileContent(file);
        if (AppConfig.isCompressionEnabled()) {
            CompressionStrategy strategy = compressionFactory.getStrategy(AppConfig.getDecompressionAlgorithm());
            content = strategy.compress(content);
        }
        String hash = hashingStrategy.hash(content);
        boolean isChunkDuplicate = chunkRepository.isChunkDuplicate(hash);
        if (!isChunkDuplicate) {
            chunkRepository.storeChunkWithHash(hash, content);
        }
        fileRepository.addChunkToFile(fileIdentifier, hash);
    }

    public void buildFile(String fileIdentifier, File outputFile) throws IOException {
        List<String> chunkHashes = fileRepository.getFileChunks(fileIdentifier);
        if (chunkHashes.isEmpty()) {
            throw new IllegalArgumentException("File does not exist!");
        }
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            for (String chunkHash : chunkHashes) {
                byte[] content = chunkRepository.getChunkByHash(chunkHash);
                if (AppConfig.isCompressionEnabled()) {
                    DecompressionStrategy strategy = decompressionFactory.getStrategy(AppConfig.getDecompressionAlgorithm());
                    content = strategy.decompress(content);
                }
                outputStream.write(content);
            }
        }
    }

    private byte[] getFileContent(File file) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            return inputStream.readAllBytes();
        }
    }
}