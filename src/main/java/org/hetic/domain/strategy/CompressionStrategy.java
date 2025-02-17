package org.hetic.domain.strategy;

import java.io.IOException;

public interface CompressionStrategy {
      byte[] compress(byte[] data) throws IOException;
}
