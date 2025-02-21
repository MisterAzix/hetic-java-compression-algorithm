package org.hetic.adapters.snappy;

import org.hetic.domain.strategy.CompressionStrategy;
import org.hetic.domain.strategy.DecompressionStrategy;
import org.xerial.snappy.Snappy;

import java.io.IOException;

public class SnappyDecompressionStrategy implements DecompressionStrategy {
    @Override
    public byte[] decompress(byte[] compressedData) {
        try {
            return Snappy.uncompress(compressedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}