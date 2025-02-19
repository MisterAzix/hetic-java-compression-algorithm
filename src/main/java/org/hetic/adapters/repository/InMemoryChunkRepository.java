package org.hetic.adapters.repository;

import org.hetic.domain.repository.ChunkRepository;
import java.util.*;

public class InMemoryChunkRepository implements ChunkRepository {
    private final List<byte[]> storage = new ArrayList<>();
    private final Map<String, byte[]> hashToChunkMap = new HashMap<>();

    @Override
    public void storeChunks(List<byte[]> chunks) {
        // Keeping this for backward compatibility
        storage.addAll(chunks);
    }

    @Override
    public boolean isChunkDuplicate(String hash) {
        return hashToChunkMap.containsKey(hash);
    }

    @Override
    public void storeChunkWithHash(byte[] chunk, String hash) {
        storage.add(chunk);
        if (!isChunkDuplicate(hash)) {
            hashToChunkMap.put(hash, chunk);
        }
    }

    public List<byte[]> getStorage() {
        return storage;
    }

    public int getUniqueChunksCount() {
        return hashToChunkMap.size();
    }
}