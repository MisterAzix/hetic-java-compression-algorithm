package org.hetic.adapters.repository;

import org.hetic.domain.repository.CompressionRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCompressionRepository implements CompressionRepository {
    private final List<byte[]> storage = new ArrayList<>();

    @Override
    public void storeCompression(byte[] compressedData) {
        storage.add(compressedData);
    }

    public List<byte[]> getStorage() {
        return new ArrayList<>(storage);
    }
}