package org.hetic.adapters.zstd;

import org.hetic.domain.strategy.DecompressionStrategy;
import com.github.luben.zstd.Zstd;


public class ZstdDecompressionStrategy implements DecompressionStrategy {
    @Override
    public byte[] decompress(byte[] compressedData) {
        return Zstd.decompress(compressedData,
                (int) Zstd.decompressedSize(compressedData));
    }
}