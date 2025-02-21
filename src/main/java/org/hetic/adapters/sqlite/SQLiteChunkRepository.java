package org.hetic.adapters.sqlite;

import org.hetic.config.SQLiteDataSourceConfig;
import org.hetic.domain.repository.ChunkRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLiteChunkRepository implements ChunkRepository {
    private final DataSource dataSource;

    public SQLiteChunkRepository() {
        this.dataSource = SQLiteDataSourceConfig.getDataSource();
    }

    @Override
    public void storeChunkWithHash(String hash, byte[] content) {
        try (Connection connection = dataSource.getConnection()) {
            if (!isChunkDuplicate(hash)) {
                String sql = "INSERT INTO chunks (hash, content) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, hash);
                    statement.setBytes(2, content);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getChunkByHash(String hash) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT content FROM chunks WHERE hash = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, hash);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getBytes("content");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean isChunkDuplicate(String hash) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT 1 FROM chunks WHERE hash = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, hash);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, byte[]> getAllChunks() {
        Map<String, byte[]> chunks = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT hash, content FROM chunks";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    chunks.put(resultSet.getString("hash"), resultSet.getBytes("content"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chunks;
    }
}