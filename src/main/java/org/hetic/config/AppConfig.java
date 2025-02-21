package org.hetic.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading application properties", ex);
        }
    }

    public static String getMode() {
        return properties.getProperty("mode", "chunking");
    }

    public static boolean isCompressionEnabled() {
        return Boolean.parseBoolean(properties.getProperty("compression.enabled", "false"));
    }

    public static String getDecompressionAlgorithm() {
        return properties.getProperty("compression.algorithm", "zstd");
    }
}