package com.cafepos;

import com.cafepos.smells.OrderManagerGod;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week6CharacterizationTests {

    @Test
    void no_discount_cash_payment() {
        String receipt = OrderManagerGod.process("ESP+SHOT+OAT", 1,
                "CASH", "NONE", false);
        assertTrue(receipt.startsWith("Order (ESP+SHOT+OAT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.80"));
        assertTrue(receipt.contains("Tax (10%): 0.38"));
        assertTrue(receipt.contains("Total: 4.18"));
    }
    @Test
    void loyalty_discount_card_payment() {
        String receipt = OrderManagerGod.process("LAT+L", 2, "CARD",
                "LOYAL5", false);
        // Latte (Large) base = 3.20 + 0.70 = 3.90, qty 2 => 7.80
        // 5% discount => 0.39, discounted=7.41; tax 10% => 0.74; total=8.15
        assertTrue(receipt.contains("Subtotal: 7.80"));
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Tax (10%): 0.74"));
        assertTrue(receipt.contains("Total: 8.15"));
    }
    @Test
    void coupon_fixed_amount_and_qty_clamp() {
        String receipt = OrderManagerGod.process("ESP+SHOT", 0, "WALLET",
                "COUPON1", false);
    // qty=0 clamped to 1; Espresso+SHOT = 2.50 + 0.80 = 3.30; coupon1 => -1 => 2.30; tax=0.23; total=2.53
        assertTrue(receipt.contains("Order (ESP+SHOT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.30"));
        assertTrue(receipt.contains("Discount: -1.00"));
        assertTrue(receipt.contains("Tax (10%): 0.23"));
        assertTrue(receipt.contains("Total: 2.53"));
    }

    @Test
    void wallet_payment_no_discount() {
        String receipt = OrderManagerGod.process("LAT", 1, "WALLET", "NONE", false);
        assertTrue(receipt.contains("Order (LAT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.20"));
        assertTrue(receipt.contains("Tax (10%): 0.32"));
        assertTrue(receipt.contains("Total: 3.52"));
    }

    @Test
    void invalid_discount_code_ignored() {
        String receipt = OrderManagerGod.process("LAT", 1, "CASH", "INVALID", false);
        assertTrue(receipt.contains("Subtotal:"));
        assertFalse(receipt.contains("Discount:"));
        assertTrue(receipt.contains("Total:"));
    }

    @Test
    void null_discount_treated_as_none() {
        String receipt = OrderManagerGod.process("ESP", 1, "CASH", null, false);
        assertTrue(receipt.contains("Subtotal: 2.50"));
        assertTrue(receipt.contains("Tax (10%): 0.25"));
        assertTrue(receipt.contains("Total: 2.75"));
    }

    @Test
    void unknown_product_fails_gracefully() {
        assertThrows(IllegalArgumentException.class, () -> {
            OrderManagerGod.process("UNKNOWN", 1, "CASH", "NONE", false);
        });
    }

    @Test
    void large_quantity_still_correct() {
        String receipt = OrderManagerGod.process("ESP", 10, "CARD", "LOYAL5", false);
        assertTrue(receipt.contains("Subtotal: 25.00"));
        assertTrue(receipt.contains("Discount: -1.25"));
        assertTrue(receipt.contains("Tax (10%): 2.38"));
        assertTrue(receipt.contains("Total: 26.13"));
    }

    @Test
    void prints_cash_payment_line() {
        var out = new java.io.ByteArrayOutputStream();
        var original = System.out;
        System.setOut(new java.io.PrintStream(out));

        OrderManagerGod.process("ESP", 1, "CASH", "NONE", false);

        System.setOut(original);
        String printed = out.toString();

        assertTrue(printed.contains("[Cash] Customer paid 2.75 EUR"));
    }

    @Test
    void prints_card_payment_line() {
        var out = new java.io.ByteArrayOutputStream();
        var original = System.out;
        System.setOut(new java.io.PrintStream(out));

        OrderManagerGod.process("ESP", 1, "CARD", "NONE", false);

        System.setOut(original);
        String printed = out.toString();

        assertTrue(printed.contains("[Card] Customer paid 2.75 EUR with card ****1234"));
    }

    @Test
    void prints_wallet_payment_line() {
        var out = new java.io.ByteArrayOutputStream();
        var original = System.out;
        System.setOut(new java.io.PrintStream(out));

        OrderManagerGod.process("LAT", 1, "WALLET", "NONE", false);

        System.setOut(original);
        String printed = out.toString();

        assertTrue(printed.contains("[Wallet] Customer paid 3.52 EUR via wallet user-wallet-789"));
    }

    @Test
    void prints_unknown_payment_type() {
        var out = new java.io.ByteArrayOutputStream();
        var original = System.out;
        System.setOut(new java.io.PrintStream(out));

        OrderManagerGod.process("ESP", 1, "shubdsa", "NONE", false);

        System.setOut(original);
        String printed = out.toString();

        assertTrue(printed.contains("[UnknownPayment]"));
        assertTrue(printed.contains("2.75"));
    }

    @Test
    void skips_print_for_null_payment_type() {
        var out = new java.io.ByteArrayOutputStream();
        var original = System.out;
        System.setOut(new java.io.PrintStream(out));

        OrderManagerGod.process("ESP", 1, null, "NONE", false);

        System.setOut(original);
        String printed = out.toString();

        assertTrue(printed.isBlank());
    }

}
