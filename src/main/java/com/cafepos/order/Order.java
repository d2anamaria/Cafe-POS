package com.cafepos.order;

import com.cafepos.common.Money;
import com.cafepos.observer.OrderObserver;
import com.cafepos.observer.OrderPublisher;
import com.cafepos.payment.PaymentStrategy;
import java.util.*;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) { this.id = id; }

    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }
    public Money taxAtPercent(int percent) {
        return this.subtotal().multiply(percent/100.00);
    }
    public Money totalWithTax(int percent) {
        return this.subtotal().add(taxAtPercent(percent));
    }


    // 1) Maintain subscriptions
    public void register(OrderObserver o) {
        if (o==null) throw new IllegalArgumentException("observer cannot be null");
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }
    public void unregister(OrderObserver o) {
        if (o == null) throw new IllegalArgumentException("observer cannot be null");
        observers.remove(o);
    }
    // 2) Publish events
    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver o : observers) {
            o.updated(order, eventType);
        }
    }

    // helper method used internally by Order itself
    private void notifyObservers(String eventType) {
        notifyObservers(this, eventType);
    }

    // 3) Hook notifications into existing behaviors
    public void addItem(LineItem li) {
        items.add(li);
        notifyObservers("itemAdded");
    }

    public void removeItem(LineItem li) {
        if(!items.isEmpty())
            items.remove(li);
        notifyObservers("itemRemoved");
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("strategy required");
        strategy.pay(this);
        notifyObservers("paid");
    }

    public void markReady() {
        notifyObservers("ready");
    }

    public String id() {
        return String.valueOf(id);
    }
    public List<LineItem> items() {
        return items;
    }
}
