package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.menu.Menu;
import com.cafepos.menu.MenuComponent;
import com.cafepos.menu.MenuItem;
import com.cafepos.state.OrderFSM;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeIteratorStateTests {

    @Test
    void depth_first_iteration_collects_all_nodes() {
        Menu root = new Menu("ROOT");
        Menu a = new Menu("A");
        Menu b = new Menu("B");
        root.add(a);
        root.add(b);
        a.add(new MenuItem("x", Money.of(1.0), true));
        b.add(new MenuItem("y", Money.of(2.0), false));
        List<String> names = root.allItems().stream().map(MenuComponent::name).toList();
        assertTrue(names.contains("x"));
        assertTrue(names.contains("y"));
    }

    @Test
    void vegetarian_items_filter() {
        Menu root = new Menu("MENU");
        root.add(new MenuItem("Salad", Money.of(5.0), true));
        root.add(new MenuItem("Burger", Money.of(8.0), false));
        List<MenuItem> vegItems = root.vegetarianItems();
        assertEquals(1, vegItems.size());
        assertTrue(vegItems.get(0).vegetarian());
    }

    @Test
    void order_fsm_happy_path() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        fsm.markReady();
        assertEquals("READY", fsm.status());
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void order_fsm_illegal_transition() {
        OrderFSM fsm = new OrderFSM();
        fsm.prepare();
        assertEquals("NEW", fsm.status());
    }
}