package org.hetic.adapters.inMemory;

import org.hetic.domain.repository.ChunkRepository;
import java.util.*;

public class InMemoryChunkRepository implements ChunkRepository {
    private final Map<String, byte[]> chunks = new HashMap<>();

    @Override
    public void storeChunkWithHash(String hash, byte[] content) {
        if (!isChunkDuplicate(hash)) {
            chunks.put(hash, content);
        }
    }

    @Override
    public byte[] getChunkByHash(String hash) {
        return chunks.get(hash);
    }

    @Override
    public boolean isChunkDuplicate(String hash) {
        return chunks.containsKey(hash);
    }

    @Override
    public Map<String, byte[]> getAllChunks() {
        return chunks;
    }
}