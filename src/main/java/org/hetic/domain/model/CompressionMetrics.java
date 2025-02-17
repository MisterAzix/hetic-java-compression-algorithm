package org.hetic.domain.model;

public record CompressionMetrics( long originalSize,
    long compressedSize) {
     public double getCompressionRatio() {
        return (double) compressedSize / originalSize;
    }
}