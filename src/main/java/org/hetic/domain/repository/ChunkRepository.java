package org.hetic.domain.repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public interface ChunkRepository {
    void storeChunkWithHash(String hash, byte[] content) throws IOException;

    byte[] getChunkByHash(String hash);

    Map<String, byte[]> getAllChunks();

    boolean isChunkDuplicate(String hash);
}