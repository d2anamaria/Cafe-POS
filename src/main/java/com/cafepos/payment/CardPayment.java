package com.cafepos.payment;

import com.cafepos.domain.Order;

import java.util.Objects;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;
    public CardPayment(String cardNumber) {
        this.cardNumber = Objects.requireNonNull(cardNumber,"Card number required fo card payment");
    }
    @Override
    public void pay(Order order) {
        System.out.println("[Card] Customer paid " + order.totalWithTax(10) + " EUR with card ****"+
                cardNumber.substring(cardNumber.length() - 4));
    }
}
