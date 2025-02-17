package org.hetic.domain.repository;

import java.io.IOException;

public interface DecompressionRepository {
    void storeDecompression(byte[] decompressedData) throws IOException;
} 
