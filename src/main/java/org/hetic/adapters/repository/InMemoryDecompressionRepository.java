package org.hetic.adapters.repository;

import java.util.ArrayList;
import java.util.List;

import org.hetic.domain.repository.DecompressionRepository;

public class InMemoryDecompressionRepository implements DecompressionRepository {

    private final List<byte[]> storage = new ArrayList<>();

    @Override
    public void storeDecompression(byte[] decompressedData) {
        storage.add(decompressedData);
    }

    public List<byte[]> getDecompressedData() {
        return new ArrayList<>(storage);
    }
}
