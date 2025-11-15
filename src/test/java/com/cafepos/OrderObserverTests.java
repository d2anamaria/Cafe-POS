package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.order.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.payment.CashPayment;
import com.cafepos.observer.OrderObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class OrderObserverTests {

    @Test
    void registering_same_observer_twice_does_not_create_duplicates() {
        var order = new Order(1);
        var product = new SimpleProduct("A", "Coffee", Money.of(2.0));
        var lineItem = new LineItem(product, 1);

        // an observer that will increment a counter when notified
        AtomicInteger counter = new AtomicInteger(0);
        OrderObserver obs = (o, e) -> counter.incrementAndGet();

        order.register(obs);
        order.register(obs);

        //this notifies all the observers in the list, so if obs was added twice, should be notified twice
        order.addItem(lineItem);

        assertEquals(1, counter.get(), "Observer should be notified only once");
    }

    @Test
    void addItem_notifies_with_itemAdded() {
        var product = new SimpleProduct("P1", "Coffee", Money.of(3.0));
        var order = new Order(10);

        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));

        order.addItem(new LineItem(product, 1));

        assertTrue(events.contains("itemAdded"));
    }

    @Test
    void pay_notifies_with_paid() {
        var product=new SimpleProduct("P2", "Latte", Money.of(4.0));
        var order=new Order(20);
        order.addItem(new LineItem(product, 1));

        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));

        order.pay(new CashPayment());

        assertTrue(events.contains("paid"));
    }

    @Test
    void multiple_observers_receive_same_event() {
        var order=new Order(31);

        List<String> events1 = new ArrayList<>();
        List<String> events2 = new ArrayList<>();

        OrderObserver obs1 = (o, evt) -> events1.add(evt);
        OrderObserver obs2 = (o, evt) -> events2.add(evt);

        order.register(obs1);
        order.register(obs2);

        order.markReady();

        assertTrue(events1.contains("ready"),
                "First observer should receive 'ready'");
        assertTrue(events2.contains("ready"),
                "Second observer should also receive 'ready'");

        // Optional: ensure both got exactly one event
        assertEquals(1, events1.size());
        assertEquals(1, events2.size());
    }

    @Test
    void register_throws_if_null_observer() {
        Order order=new Order(1);

        assertThrows(IllegalArgumentException.class, () -> order.register(null),
                "Registering a null observer should throw IllegalArgumentException");
    }

    @Test
    void unregister_throws_if_null_observer() {
        Order order=new Order(2);

        assertThrows(IllegalArgumentException.class, ()->order.unregister(null),
                "Unregistering a null observer should throw IllegalArgumentException");
    }
}
