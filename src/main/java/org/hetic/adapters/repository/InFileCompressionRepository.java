package org.hetic.adapters.repository;

import org.hetic.domain.repository.CompressionRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class InFileCompressionRepository implements CompressionRepository {
    private final String outputPath;
    private String filePath;

    public InFileCompressionRepository(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void storeCompression(byte[] compressedData) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        this.filePath = outputPath + "/compressed" + timestamp + ".bin";
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(compressedData);
        }
    }

    public String getFilePath() {
        return filePath;
    }
}