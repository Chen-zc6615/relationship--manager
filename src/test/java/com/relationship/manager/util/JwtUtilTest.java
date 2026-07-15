package com.relationship.manager.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void shouldGenerateAndParseToken() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 3_600_000);

        String token = jwtUtil.generateToken(42L);

        assertEquals(42L, jwtUtil.parseUserId(token));
        assertEquals(3_600L, jwtUtil.getExpirationSeconds());
    }

    @Test
    void shouldRejectShortSecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("too-short", 3_600_000));
    }
}
