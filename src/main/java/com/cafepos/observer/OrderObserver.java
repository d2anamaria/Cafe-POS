package com.cafepos.observer;

import com.cafepos.order.Order;

public interface OrderObserver {
    void updated(Order order, String eventType);
}
