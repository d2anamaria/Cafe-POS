package com.cafepos.checkout;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.decorator.Priced;
import com.cafepos.order.LineItem;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.order.Order;

public final class CheckoutService {
    private final ProductFactory factory;
    private final PricingService pricing;
    private final ReceiptPrinter printer;
    private final FixedRateTaxPolicy taxPolicy;

    public CheckoutService(ProductFactory factory, PricingService pricing, ReceiptPrinter printer, FixedRateTaxPolicy taxPolicy) {
        this.factory = factory;
        this.pricing = pricing;
        this.printer = printer;
        this.taxPolicy = taxPolicy;
    }

    public String checkout(String recipe, int qty, PaymentStrategy payment) {
        Product product = factory.create(recipe);
        if (qty <= 0) qty = 1;
        Money unit = (product instanceof Priced p) ? p.price() : product.basePrice();
        Money subtotal = unit.multiply(qty);
        var result = pricing.price(subtotal);

        Order order = new Order(System.currentTimeMillis());
        order.addItem(new LineItem(product, qty));

        payment.pay(order);

        return printer.format(recipe, qty, result, taxPolicy);
    }
}