package com.cafepos.catalog;

import com.cafepos.common.Money;

import java.util.Objects;

public final class SimpleProduct implements Product, Priced {
    private final String id;
    private final String name;
    private final Money basePrice;

    public SimpleProduct(String id, String name, Money basePrice) {
        this.id = Objects.requireNonNull(id, "id required for product");
        this.name = Objects.requireNonNull(name, "name required for product");
        this.basePrice = Objects.requireNonNull(basePrice, "basePrice required for product");
    }

    @Override public String id() { return id; }
    @Override public String name() { return name; }
    @Override public Money basePrice() { return basePrice; }
    @Override public Money price() { return basePrice; }
}
