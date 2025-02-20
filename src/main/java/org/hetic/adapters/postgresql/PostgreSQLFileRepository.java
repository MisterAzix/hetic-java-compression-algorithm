package org.hetic.adapters.postgresql;

import org.hetic.domain.repository.FileRepository;
import org.hetic.config.DataSourceConfig;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostgreSQLFileRepository implements FileRepository {
    private final DataSource dataSource;

    public PostgreSQLFileRepository() {
        this.dataSource = DataSourceConfig.getDataSource();
    }

    @Override
    public void addChunkToFile(String fileIdentifier, String hash) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO file_chunks (file_identifier, chunk_hash) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, fileIdentifier);
                statement.setString(2, hash);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getFileChunks(String fileIdentifier) {
        List<String> chunks = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT chunk_hash FROM file_chunks WHERE file_identifier = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, fileIdentifier);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    chunks.add(resultSet.getString("chunk_hash"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chunks.isEmpty() ? Collections.emptyList() : chunks;
    }
}