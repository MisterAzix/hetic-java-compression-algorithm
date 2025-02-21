package org.hetic.domain.factory;

import org.hetic.adapters.snappy.SnappyCompressionStrategy;
import org.hetic.adapters.zstd.ZstdCompressionStrategy;
import org.hetic.domain.strategy.CompressionStrategy;

public class CompressionFactory {
    private final SnappyCompressionStrategy snappyCompressionStrategy;
    private final ZstdCompressionStrategy zstdCompressionStrategy;

    public CompressionFactory() {
        this.snappyCompressionStrategy = new SnappyCompressionStrategy();
        this.zstdCompressionStrategy = new ZstdCompressionStrategy();
    }

    public CompressionStrategy getStrategy(String strategy) {
        return switch (strategy) {
            case "snappy" -> snappyCompressionStrategy;
            case "zstd" -> zstdCompressionStrategy;
            default -> throw new IllegalArgumentException("Invalid compression strategy");
        };
    }
}
