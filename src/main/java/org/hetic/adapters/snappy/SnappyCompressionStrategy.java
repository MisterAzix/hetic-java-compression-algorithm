package org.hetic.adapters.snappy;

import org.hetic.domain.strategy.CompressionStrategy;
import org.xerial.snappy.Snappy;

import java.io.IOException;

public class SnappyCompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }
}