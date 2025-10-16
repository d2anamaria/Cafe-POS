package com.cafepos;

import com.cafepos.smells.OrderManagerGod;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week6CharacterizationTests {

    @Test
    void no_discount_cash_payment() {
        String receipt = OrderManagerGod.process("ESP+SHOT+OAT", 1, "CASH", "NONE", false);
        assertTrue(receipt.startsWith("Order (ESP+SHOT+OAT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.80"));
        assertTrue(receipt.contains("Tax (10%): 0.38"));
        assertTrue(receipt.contains("Total: 4.18"));
    }

    @Test
    void loyalty_discount_card_payment() {
        String receipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);
        assertTrue(receipt.contains("Subtotal: 7.80"));
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Tax (10%): 0.74"));
        assertTrue(receipt.contains("Total: 8.15"));
    }

    @Test
    void coupon_fixed_amount_and_qty_clamp() {
        String receipt = OrderManagerGod.process("ESP+SHOT", 0, "WALLET", "COUPON1", false);
        assertTrue(receipt.contains("Order (ESP+SHOT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.30"));
        assertTrue(receipt.contains("Discount: -1.00"));
        assertTrue(receipt.contains("Tax (10%): 0.23"));
        assertTrue(receipt.contains("Total: 2.53"));
    }
}