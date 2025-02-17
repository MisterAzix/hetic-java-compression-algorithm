package org.hetic.adapters.strategy;

import org.hetic.domain.strategy.CompressionStrategy;
import com.github.luben.zstd.Zstd;


public class ZstdCompressionStrategy implements CompressionStrategy {
    @Override
    public byte[] compress(byte[] data) {
        return Zstd.compress(data);
    }
}