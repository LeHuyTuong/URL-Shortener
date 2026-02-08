package com.urlshort.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    @Test
    void nextId_shouldReturnUniqueIds() {
        IdGenerator generator = new IdGenerator(1);

        long id1 = generator.nextId();
        long id2 = generator.nextId();
        long id3 = generator.nextId();

        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
    }

    @Test
    void nextId_shouldIncludesMachineId() {
        int machineId = 5;
        IdGenerator generator = new IdGenerator(machineId);

        long id = generator.nextId();

        // Extract machineId from high 16 bits
        int extractedMachineId = (int) (id >> 48);
        assertEquals(machineId, extractedMachineId);
    }

    @Test
    void nextId_sequenceShouldIncrement() {
        IdGenerator generator = new IdGenerator(0);

        long id1 = generator.nextId();
        long id2 = generator.nextId();

        // With machineId=0, the IDs should be 1, 2, 3...
        assertEquals(1, id1);
        assertEquals(2, id2);
    }

    @Test
    void differentMachines_shouldGenerateDifferentIds() {
        IdGenerator gen1 = new IdGenerator(1);
        IdGenerator gen2 = new IdGenerator(2);

        long id1 = gen1.nextId();
        long id2 = gen2.nextId();

        assertNotEquals(id1, id2);
    }
}
