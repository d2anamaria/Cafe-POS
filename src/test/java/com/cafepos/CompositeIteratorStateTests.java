package com.cafepos;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.decorator.Priced;
import com.cafepos.factory.ProductFactory;
import com.cafepos.menu.Menu;
import com.cafepos.menu.MenuComponent;
import com.cafepos.menu.MenuItem;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
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
    void composite_iterator_depth_first_order() {
        Menu root = new Menu("Root");
        Menu drinks = new Menu("Drinks");
        Menu coffee = new Menu("Coffee");
        MenuItem espresso = new MenuItem("Espresso", Money.of(2.5), true);
        MenuItem latte = new MenuItem("Latte", Money.of(3.5), true);

        coffee.add(espresso);
        drinks.add(coffee);
        drinks.add(latte);
        root.add(drinks);

        var names = root.allItems().stream().map(MenuComponent::name).toList();

        assertEquals(List.of("Drinks", "Coffee", "Espresso", "Latte"), names);
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

        OrderFSM fsm2 = new OrderFSM();
        fsm2.pay();
        fsm2.cancel();
        assertEquals("CANCELLED", fsm2.status());
    }

    @Test
    void order_fsm_illegal_transition() {
        OrderFSM fsm = new OrderFSM();

        // NEW state
        fsm.prepare();
        fsm.markReady();
        fsm.deliver();
        assertEquals("NEW", fsm.status()); //same after trying all illegal actions

        //  PREPARING
        fsm.pay();

        fsm.pay();
        fsm.prepare();
        fsm.deliver();
        assertEquals("PREPARING", fsm.status());

        // READY
        fsm.markReady();

        fsm.prepare();
        fsm.markReady();
        fsm.pay();
        fsm.cancel();
        assertEquals("READY", fsm.status());

        // DELIVERED
        fsm.deliver();

        fsm.prepare();
        fsm.markReady();
        fsm.pay();
        fsm.cancel();
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());


        //CANCELLED
        fsm = new OrderFSM();
        fsm.prepare();
        fsm.cancel();
        fsm.pay();
        fsm.prepare();
        fsm.markReady();
        fsm.deliver();
        assertEquals("CANCELLED", fsm.status());
    }


    @Test
    void menu_built_from_factory_and_money_matches() {
        ProductFactory factory = new ProductFactory();

        Menu drinks = new Menu("Drinks");
        Product espresso = factory.create("ESP");
        Product oatLatte = factory.create("LAT+OAT");
        Product espressoShot = factory.create("ESP+SHOT");

        drinks.add(new MenuItem(espresso.name(), ((Priced) espresso).price(), true));
        drinks.add(new MenuItem(oatLatte.name(), ((Priced) oatLatte).price(), true));
        drinks.add(new MenuItem(espressoShot.name(), ((Priced) espressoShot).price(), true));

        var names = drinks.allItems().stream().map(MenuComponent::name).toList();
        assertTrue(names.contains("Espresso + Extra Shot"));
        assertTrue(names.contains("Latte + Oat Milk"));

        MenuItem found = drinks.allItems().stream()
                .filter(mc -> mc.name().equals("Espresso + Extra Shot"))
                .map(mc -> (MenuItem) mc)
                .findFirst()
                .orElseThrow();

        assertEquals(Money.of(3.30), found.price());

        Order order = new Order(1);
        order.addItem(new LineItem(espressoShot, 2));
        assertEquals(Money.of(6.60), order.subtotal());
        assertEquals(Money.of(0.66), order.taxAtPercent(10));
        assertEquals(Money.of(7.26), order.totalWithTax(10));
    }
}