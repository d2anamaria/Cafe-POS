package com.cafepos.payment;

import com.cafepos.order.Order;

import java.util.Objects;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;
    public CardPayment(String cardNumber) {
        this.cardNumber = Objects.requireNonNull(cardNumber,"Card number required for card payment");
    }
    @Override
    public void pay(Order order) {
        System.out.println("[Card] Customer paid " + order.totalWithTax(10) + " EUR with card ****"+
                cardNumber.substring(cardNumber.length() - 4));
    }
}
