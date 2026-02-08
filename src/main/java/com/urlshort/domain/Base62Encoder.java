package com.urlshort.domain;

/**
 * Base62 Encoder/Decoder for URL Shortener.
 * Charset: a-z, A-Z, 0-9 (letters first for better readability)
 */
public class Base62Encoder {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARSET.length(); // 62

    /**
     * Encode a number to Base62 string.
     * Example: encode(10) -> "k"
     * 
     * TODO: Implement this method
     * Hint: Use modulo (%) and division (/) operations
     */
    public static String encode(long num) {
        if (num == 0) {
            return String.valueOf(CHARSET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            // sb thêm thằng đầu tiên vào cuối
            sb.append(CHARSET.charAt((int) (num % 62)));
            // thêm xong rồi chia cho 62 cho đến hết
            num /= 62;

            // Bước	    num	    num % 62	Ký tự	num / 62
            // 1	        125	    1	        b	    2
            // 2	        2	    2	        c	    0
            // Kết quả			            "cb" → reverse = "bc"	
        }

        // return lại chuỗi đảo ngược
        return sb.reverse().toString();
    }

    /**
     * Decode a Base62 string back to number.
     * Example: decode("k") -> 10
     * 
     * TODO: Implement this method (bonus)
     */
    public static long decode(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            result = result * 62 + CHARSET.indexOf(str.charAt(i));
        }
        // Bước	Ký tự	Index	Công thức	result
        // 1	b	    1	    0 × 62 + 1	1
        // 2	c	    2	    1 × 62 + 2	64
        return result;
    }
}
