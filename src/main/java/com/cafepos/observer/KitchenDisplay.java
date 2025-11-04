package com.cafepos.observer;

import com.cafepos.order.Order;

public class KitchenDisplay implements OrderObserver{
    @Override
    public void updated(Order order,String eventType) {
        if ("itemAdded".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": item added");
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Payment received");
        }    }
}
