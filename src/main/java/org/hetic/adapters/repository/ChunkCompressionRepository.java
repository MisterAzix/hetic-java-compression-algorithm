package org.hetic.adapters.repository;

import org.hetic.domain.repository.CompressionRepository;
import org.hetic.domain.model.CompressionMetrics;
import org.hetic.adapters.strategy.ZstdCompressionStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ChunkCompressionRepository implements CompressionRepository {
    private final ZstdCompressionStrategy compressionStrategy;
    private final String outputDirectory;
    private final List<byte[]> compressedChunks;
    private long totalOriginalSize = 0;
    private long totalCompressedSize = 0;

    public ChunkCompressionRepository(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.compressionStrategy = new ZstdCompressionStrategy();
        this.compressedChunks = new ArrayList<>();
    }

    @Override
    public byte[] compress(byte[] data) throws IOException {
        byte[] compressed = compressionStrategy.compress(data);
        compressedChunks.add(compressed);

        totalOriginalSize += data.length;
        totalCompressedSize += compressed.length;

        // Save chunk to file
        String chunkFileName = String.format("chunk_%d.bin", compressedChunks.size() - 1);
        File outputFile = new File(outputDirectory, chunkFileName);
        Files.write(outputFile.toPath(), compressed);

        return compressed;
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        return compressionStrategy.decompress(compressedData);
    }

    @Override
    public CompressionMetrics getMetrics() {
        return new CompressionMetrics(totalOriginalSize, totalCompressedSize);
    }

    public List<byte[]> getCompressedChunks() {
        return new ArrayList<>(compressedChunks);
    }
}