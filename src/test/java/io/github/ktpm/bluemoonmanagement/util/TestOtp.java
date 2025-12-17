package io.github.ktpm.bluemoonmanagement.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestOtp {
    @Test
    void testGenerateOtpLengthAndRange() {
        for (int i = 0; i < 100; i++) {
            String otp = OtpUtil.generateOtp();
            assertEquals(6, otp.length());
            int value = Integer.parseInt(otp);
            assertTrue(value >= 100000 && value <= 999999);
        }
    }
}
