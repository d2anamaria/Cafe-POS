package com.cafepos;

import com.cafepos.smells.OrderManagerGod;
import com.cafepos.checkout.CheckoutService;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import com.cafepos.common.Money;
import com.cafepos.payment.CashPayment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DiscountPolicyCharacterizationTests {

    @Test
    void loyal5_discount_characterization() {
        String oldReceipt = OrderManagerGod.process("ESP", 2, "CASH", "LOYAL5", false);
        assertTrue(oldReceipt.contains("Discount: -"));
        assertTrue(oldReceipt.contains("Total: 5.23"));

        var taxPolicy = new FixedRateTaxPolicy(10);
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), taxPolicy);
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, taxPolicy);
        String newReceipt = checkout.checkout("ESP", 2, new CashPayment());

        assertEquals(oldReceipt, newReceipt);
    }

    @Test
    void coupon1_discount_characterization() {
        String oldReceipt = OrderManagerGod.process("ESP", 1, "CASH", "COUPON1", false);
        assertTrue(oldReceipt.contains("Discount: -1.00"));
        assertTrue(oldReceipt.contains("Total: 1.65"));

        var taxPolicy = new FixedRateTaxPolicy(10);
        var pricing = new PricingService(new FixedCouponDiscount(Money.of(1.00)), taxPolicy);
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, taxPolicy);
        String newReceipt = checkout.checkout("ESP", 1, new CashPayment());

        assertEquals(oldReceipt, newReceipt);
    }

    @Test
    void unknown_discount_characterization() {
        String oldReceipt = OrderManagerGod.process("ESP", 1, "CASH", "UNKNOWN", false);
        assertFalse(oldReceipt.contains("Discount:"));
        assertTrue(oldReceipt.contains("Total: 2.75"));

        var taxPolicy = new FixedRateTaxPolicy(10);
        var pricing = new PricingService(new NoDiscount(), taxPolicy);
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, taxPolicy);
        String newReceipt = checkout.checkout("ESP", 1, new CashPayment());

        assertEquals(oldReceipt, newReceipt);
    }
}