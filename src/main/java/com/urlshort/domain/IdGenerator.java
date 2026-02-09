package com.urlshort.domain;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Snowflake-like ID Generator for distributed systems.
 * 
 * Structure (64-bit):
 * - Bits 63-48: Machine ID (16 bits = 65536 machines)
 * - Bits 47-0: Sequence (48 bits = 281 trillion per machine)
 */
public class IdGenerator {

    private final int machineId;
    private final AtomicLong sequence = new AtomicLong(0);

    public IdGenerator(int machineId) {
        if (machineId < 0 || machineId > 65535) {
            throw new IllegalArgumentException("Machine ID must be between 0 and 65535");
        }
        this.machineId = machineId;
    }

    /**
     * Generate next unique ID.
     * Format: (machineId << 48) | sequence
     */
    public long nextId() {
        // tăng từ 0 - 1 - 2 - 3 mỗi lần , thread-safe
        long seq = sequence.incrementAndGet();
        return ((long) machineId << 48) | seq; // Đẩy machineId lên 48 bit cao, để trống 48 bit thấp cho sequence
    }

    public int getMachineId() {
        return machineId;
    }
}
