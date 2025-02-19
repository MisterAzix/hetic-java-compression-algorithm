package org.hetic.domain.repository;

import java.io.IOException;
import java.util.List;

public interface ChunkRepository {
    void storeChunks(List<byte[]> chunks) throws IOException;
    boolean isChunkDuplicate(String hash);
    void storeChunkWithHash(byte[] chunk, String hash) throws IOException;
}