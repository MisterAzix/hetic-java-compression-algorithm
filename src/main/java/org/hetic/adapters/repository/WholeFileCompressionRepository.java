package org.hetic.adapters.repository;

import org.hetic.domain.repository.CompressionRepository;
import org.hetic.domain.model.CompressionMetrics;
import org.hetic.adapters.strategy.ZstdCompressionStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WholeFileCompressionRepository implements CompressionRepository {
    private final ZstdCompressionStrategy compressionStrategy;
    private final String outputDirectory;
    private byte[] compressedFile;
    private long originalSize = 0;
    private long compressedSize = 0;

    public WholeFileCompressionRepository(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.compressionStrategy = new ZstdCompressionStrategy();
    }

    @Override
    public byte[] compress(byte[] data) throws IOException {
        compressedFile = compressionStrategy.compress(data);

        originalSize = data.length;
        compressedSize = compressedFile.length;

        // Save compressed file
        File outputFile = new File(outputDirectory, "compressed_whole.bin");
        Files.write(outputFile.toPath(), compressedFile);

        return compressedFile;
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        return compressionStrategy.decompress(compressedData);
    }

    @Override
    public CompressionMetrics getMetrics() {
        return new CompressionMetrics(originalSize, compressedSize);
    }

    public byte[] getCompressedFile() {
        return compressedFile.clone();
    }
}