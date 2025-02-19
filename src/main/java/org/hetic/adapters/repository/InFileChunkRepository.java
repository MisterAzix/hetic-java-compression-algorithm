package org.hetic.adapters.repository;

import org.hetic.domain.repository.ChunkRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InFileChunkRepository implements ChunkRepository {
    private final Map<String, String> hashToFileMap = new HashMap<>();
    private final String chunkDirectory = "chunks/";
    private int chunkCounter = 0;

    public InFileChunkRepository() {
        new File(chunkDirectory).mkdirs();
    }

    @Override
    public void storeChunks(List<byte[]> chunks) throws IOException {
        for (byte[] chunk : chunks) {
            String filename = "chunk_" + chunkCounter++ + ".bin";
            try (OutputStream outputStream = new FileOutputStream(chunkDirectory + filename)) {
                outputStream.write(chunk);
            }
        }
    }

    @Override
    public boolean isChunkDuplicate(String hash) {
        return hashToFileMap.containsKey(hash);
    }

    @Override
    public void storeChunkWithHash(byte[] chunk, String hash) throws IOException {
        if (!isChunkDuplicate(hash)) {
            String filename = "chunk_" + chunkCounter++ + ".bin";
            try (OutputStream outputStream = new FileOutputStream(chunkDirectory + filename)) {
                outputStream.write(chunk);
            }
            hashToFileMap.put(hash, filename);
        }
    }
}