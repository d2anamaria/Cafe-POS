package com.cafepos.demo;

import com.cafepos.payment.CardPayment;
import com.cafepos.smells.OrderManagerGod;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import com.cafepos.checkout.CheckoutService;

public final class Week6Demo {
    public static void main(String[] args) {
        // Old behavior
        String oldReceipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);

        // New behavior with equivalent result
        var discount = new LoyaltyPercentDiscount(5);
        var taxPolicy = new FixedRateTaxPolicy(10);
        var pricing = new PricingService(discount, taxPolicy);
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, taxPolicy);

        var payment = new CardPayment("1234567812341234");
        String newReceipt = checkout.checkout("LAT+L", 2, payment);

        System.out.println("Old Receipt:\n" + oldReceipt);
        System.out.println("\nNew Receipt:\n" + newReceipt);
        System.out.println("\nMatch: " + oldReceipt.equals(newReceipt));
    }
}
