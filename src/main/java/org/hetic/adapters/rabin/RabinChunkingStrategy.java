package org.hetic.adapters.rabin;

import org.hetic.domain.model.Chunk;
import org.hetic.domain.strategy.ChunkingStrategy;
import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RabinChunkingStrategy implements ChunkingStrategy {
    private static final int MIN_CHUNK_SIZE = 64;
    private static final int MAX_CHUNK_SIZE = 256;
    private static final int WINDOW_SIZE = 48;
    private static final long MASK = (1L << 7) - 1;


    private final RabinFingerprintLongWindowed rabin;

    public RabinChunkingStrategy() {
        Polynomial polynomial = Polynomial.createFromLong(10923124345206883L);
        this.rabin = new RabinFingerprintLongWindowed(polynomial, WINDOW_SIZE);
    }

    @Override
    public List<Chunk> chunk(InputStream input) throws IOException {
        List<Chunk> chunks = new ArrayList<>();
        ByteArrayOutputStream currentChunk = new ByteArrayOutputStream();
        byte[] buffer = new byte[WINDOW_SIZE];
        int bytesRead;
        int currentSize = 0;

        bytesRead = input.read(buffer);
        if (bytesRead > 0) {
            for (int i = 0; i < bytesRead; i++) {
                rabin.pushByte(buffer[i]);
                currentChunk.write(buffer[i]);
            }
            currentSize = bytesRead;
        }

        while ((bytesRead = input.read(buffer)) > 0) {
            for (int i = 0; i < bytesRead; i++) {
                byte b = buffer[i];
                rabin.pushByte(b);
                currentChunk.write(b);
                currentSize++;

                if (currentSize >= MIN_CHUNK_SIZE) {
                    if (isChunkBoundary(rabin.getFingerprintLong()) || currentSize >= MAX_CHUNK_SIZE) {
                        chunks.add(Chunk.from(currentChunk.toByteArray()));
                        currentChunk.reset();
                        currentSize = 0;
                    }
                }
            }
        }

        if (currentSize > 0) {
            chunks.add(Chunk.from(currentChunk.toByteArray()));
        }

        return chunks;
    }

    private boolean isChunkBoundary(long fingerprint) {
        return (fingerprint & MASK) == 0;
    }
}