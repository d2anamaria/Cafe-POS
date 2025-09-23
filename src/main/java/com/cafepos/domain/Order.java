package com.cafepos.domain;

import com.cafepos.common.Money;

import java.util.*;

public final class Order {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    public Order(long id) { this.id = id; }
    public void addItem(LineItem li) {
        items.add(li);
    }
    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }
    public Money taxAtPercent(int percent) {
        return this.subtotal().multiply(percent/100.00);
    }
    public Money totalWithTax(int percent) {
        return this.subtotal().add(taxAtPercent(percent));
    }

    public String id() {
        return String.valueOf(id);
    }

    public List<LineItem> items() {
        return items;
    }
}
