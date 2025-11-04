package com.cafepos;

import com.cafepos.printing.*;
import org.junit.jupiter.api.Test;

public class AdapterTests {

    static class FakeLegacy {
        int lastLen = -1;
        public void legacyPrint(byte[] payload) {
            lastLen = payload.length;
        }
    }

    static class TestAdapter implements Printer {
        private final FakeLegacy fake;
        public TestAdapter(FakeLegacy fake) { this.fake = fake; }
        @Override public void print(String receiptText) {
            byte[] escpos = receiptText.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            fake.legacyPrint(escpos);
        }
    }

    @Test
    void adapter_converts_text_to_bytes() {
        var fake = new FakeLegacy();
        com.cafepos.printing.Printer p = new TestAdapter(fake);
        p.print("ABC");
        org.junit.jupiter.api.Assertions.assertTrue(fake.lastLen >= 3);
    }
}

