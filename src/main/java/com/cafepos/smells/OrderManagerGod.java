package com.cafepos.smells;


import com.cafepos.common.Money;
import com.cafepos.decorator.Priced;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;


public class OrderManagerGod {
    // God Class — too many responsibilities: product creation, order creation, pricing, discounts, tax, payment, receipt printing
    // Feature Envy —  OrderManagerGod uses too many Money operations

    // Global/Static State: `LAST_DISCOUNT_CODE`, `TAX_PERCENT` are global — risky and hard to test
    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = null;

    // Data Clump — parameters will always be passed together
    // Long Method that handles all responsibilities
    public static String process(String recipe, int qty, String paymentType, String discountCode, boolean printReceipt) {

        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);
        Money unitPrice;
        // Feature Envy — logic depends on Product internals
        // Inappropriate Intimacy
        try {
            var priced = product instanceof Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }


        if (qty <= 0) qty = 1;
        Money subtotal = unitPrice.multiply(qty);

        // Feature Envy / Shotgun Surgery: Tax/discount rules embedded inline; any change requires editing this method.
        // Primitive Obsession -discountCode represents a behaviour, should be an obj/ use delegation, not a string
        Money discount = Money.zero();
        if (discountCode != null) {
            if (discountCode.equalsIgnoreCase("LOYAL5")) {          // Primitive Obsession
                // Shotgun Surgery risk -if there would be multiple discount values and the discount
                // calculation behaviour would change, small edits should be made in multiple places
                // would also result in Duplicate Code
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));            //Feature Envy & Duplicated Logic: Money and BigDecimal manipulations scattered inline.
            } else if (discountCode.equalsIgnoreCase("COUPON1")) { // Primitive Obsession
                discount = Money.of(1.00);                               // Shotgun Surgery risk
            } else if (discountCode.equalsIgnoreCase("NONE")) {    // Primitive Obsession
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode;
        }

        // Shotgun Surgery risk
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));  //Feature Envy & Duplicated Logic: Money and BigDecimal manipulations scattered inline.
        if (discounted.asBigDecimal().signum() < 0) discounted = Money.zero();

        // Shotgun Surgery risk
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))                             //Feature Envy & Duplicated Logic: Money and BigDecimal manipulations scattered inline.
                .divide(java.math.BigDecimal.valueOf(100)));
        var total = discounted.add(tax);


        // Duplicate code when testing each payment type - shouldn't have big if-else block, should use delegation
        // Primitive Obsession -paymentType represents a behaviour, should be an obj, not a string
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }


        // Feature Envy - receipt generation should be handled by another class
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);


        String out = receipt.toString();
        if (printReceipt) {
            System.out.println(out); // printing mixed with business logic
        }
        return out;
    }
}