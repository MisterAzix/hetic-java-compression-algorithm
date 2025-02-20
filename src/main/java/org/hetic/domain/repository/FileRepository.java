package org.hetic.domain.repository;

import java.sql.SQLException;
import java.util.List;

public interface FileRepository {
    void addChunkToFile(String fileIdentifier, String hash);

    List<String> getFileChunks(String fileIdentifier);
}
