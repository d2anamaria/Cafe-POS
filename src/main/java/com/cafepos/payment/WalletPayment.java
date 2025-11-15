package com.cafepos.payment;

import com.cafepos.domain.Order;

import java.util.Objects;

public final class WalletPayment implements PaymentStrategy {
    private final String walletId;

    public WalletPayment(String walletId) {
        this.walletId = Objects.requireNonNull(walletId, "walletId required for Wallet Payment");
    }

    @Override
    public void pay(Order order) {
        System.out.println("[Wallet] Customer paid " +
                order.totalWithTax(10) + " EUR via wallet " + walletId);
    }
}