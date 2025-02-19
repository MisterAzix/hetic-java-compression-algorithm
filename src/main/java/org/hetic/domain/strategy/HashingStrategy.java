package org.hetic.domain.strategy;

public interface HashingStrategy {
    String hash(byte[] content);
}