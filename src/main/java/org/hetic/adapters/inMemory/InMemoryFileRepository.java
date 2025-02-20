package org.hetic.adapters.inMemory;

import org.hetic.domain.repository.FileRepository;

import java.util.*;

public class InMemoryFileRepository implements FileRepository {
    private final Map<String, List<String>> files = new HashMap<>();

    @Override
    public void addChunkToFile(String fileIdentifier, String hash) {
        files.computeIfAbsent(fileIdentifier, k -> new ArrayList<>()).add(hash);
    }

    @Override
    public List<String> getFileChunks(String fileIdentifier) {
        return files.getOrDefault(fileIdentifier, Collections.emptyList());
    }
}