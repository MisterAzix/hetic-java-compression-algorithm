package org.hetic.domain.strategy;

public interface DecompressionStrategy {
    byte[] decompress(byte[] compressedData);
}
