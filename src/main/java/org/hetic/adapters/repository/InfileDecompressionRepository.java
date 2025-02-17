package org.hetic.adapters.repository;

import org.hetic.domain.repository.DecompressionRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class InfileDecompressionRepository implements DecompressionRepository {
    private final String outputPath;

    public InfileDecompressionRepository(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void storeDecompression(byte[] decompressedData) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        try (OutputStream outputStream = new FileOutputStream(outputPath + "/decompressed" + timestamp + ".txt")) {
            outputStream.write(decompressedData);
        }
    }
}