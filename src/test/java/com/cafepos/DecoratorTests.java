package com.cafepos;

import com.cafepos.catalog.Priced;
import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.decorator.*;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoratorTests {

    @Test
    void decorator_single_addon() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        assertEquals("Espresso + Extra Shot", withShot.name());
        assertEquals(Money.of(3.30), ((Priced) withShot).price());
    }

    @Test
    void decorator_stacks() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));
        assertEquals("Espresso + Extra Shot + Oat Milk (Large)", decorated.name());
        assertEquals(Money.of(4.50), ((Priced) decorated).price());
    }

    @Test
    void factory_parses_recipe() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");
        assertTrue(p.name().contains("Espresso") && p.name().contains("Oat Milk"));
    }

    @Test
    void order_uses_decorated_price() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));
        assertEquals(Money.of(6.60), o.subtotal());
    }

    @Test
    void decoration_order_independence() {
        Product espresso1 = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product order1 = new OatMilk(new ExtraShot(espresso1));


        Product espresso2 = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product order2 = new ExtraShot(new OatMilk(espresso2));

        assertEquals(((Priced) order1).price(), ((Priced) order2).price());
        assertTrue(order1.name().contains("Extra Shot"));
        assertTrue(order1.name().contains("Oat Milk"));
        assertTrue(order2.name().contains("Extra Shot"));
        assertTrue(order2.name().contains("Oat Milk"));
    }
}