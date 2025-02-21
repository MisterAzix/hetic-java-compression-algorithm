package org.hetic.domain.factory;

import org.hetic.adapters.snappy.SnappyDecompressionStrategy;
import org.hetic.adapters.zstd.ZstdDecompressionStrategy;
import org.hetic.domain.strategy.DecompressionStrategy;

public class DecompressionFactory {
    private final SnappyDecompressionStrategy snappyDecompressionStrategy;
    private final ZstdDecompressionStrategy zstdDecompressionStrategy;

    public DecompressionFactory() {
        this.snappyDecompressionStrategy = new SnappyDecompressionStrategy();
        this.zstdDecompressionStrategy = new ZstdDecompressionStrategy();
    }

    public DecompressionStrategy getStrategy(String strategy) {
        return switch (strategy) {
            case "snappy" -> snappyDecompressionStrategy;
            case "zstd" -> zstdDecompressionStrategy;
            default -> throw new IllegalArgumentException("Invalid decompression strategy");
        };
    }
}
