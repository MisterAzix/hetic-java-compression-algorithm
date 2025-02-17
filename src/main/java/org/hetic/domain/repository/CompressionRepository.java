package org.hetic.domain.repository;

import java.io.IOException;


public interface CompressionRepository {
    void storeCompression(byte[] compressedData) throws IOException;
}