package com.urlshort.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    void encode_zero_returnsFirstChar() {
        // Given charset starts with 'a'
        // encode(0) should return "a"
        assertEquals("a", Base62Encoder.encode(0));
    }

    @Test
    void encode_ten_returnsK() {
        // Index 10 in charset "abcdefghijklmnopqrstuvwxyz..." is 'k'
        assertEquals("k", Base62Encoder.encode(10));
    }

    @Test
    void encode_61_returnsLastChar() {
        // Index 61 is '9' (last char in charset)
        assertEquals("9", Base62Encoder.encode(61));
    }

    @Test
    void encode_62_returnsTwoChars() {
        // 62 = 1*62 + 0 -> "ba"
        assertEquals("ba", Base62Encoder.encode(62));
    }

    @Test
    void encode_largeNumber() {
        // 238328 = 62^3 = encode should be "baaa"
        // Actually: 62^3 = 238328, 238328 / 62 = 3844, 3844 / 62 = 62, 62 / 62 = 1
        // Let's use a simpler case: 3844 = 62^2 = "baa"
        long num = 62 * 62; // 3844
        assertEquals("baa", Base62Encoder.encode(num));
    }

    @Test
    void decode_shouldReverseEncode() {
        // Round-trip test
        long original = 123456789L;
        String encoded = Base62Encoder.encode(original);
        assertEquals(original, Base62Encoder.decode(encoded));
    }
}
