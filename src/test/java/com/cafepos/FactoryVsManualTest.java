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

class FactoryVsManualTest {

    @Test
    void factory_vs_manual_construction() {
        ProductFactory factory = new ProductFactory();
        Product viaFactory = factory.create("ESP+SHOT+OAT+L");


        Product viaManual = new SizeLarge(
                new OatMilk(new ExtraShot(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
                        )
                )
        );

        assertEquals(viaManual.name(), viaFactory.name());
        assertEquals(((Priced) viaManual).price(), ((Priced) viaFactory).price());

        Order order1 = new Order(1);
        order1.addItem(new LineItem(viaFactory, 1));

        Order order2 = new Order(2);
        order2.addItem(new LineItem(viaManual, 1));

        assertEquals(order1.subtotal(), order2.subtotal());
        assertEquals(order1.totalWithTax(10), order2.totalWithTax(10));
    }
    @Test
    void factory_handles_case_insensitive_input() {
        ProductFactory factory = new ProductFactory();
        Product upper = factory.create("ESP+SHOT+OAT");
        Product lower = factory.create("esp+shot+oat");
        Product mixed = factory.create("EsP+ShOt+oAt");
        assertEquals(upper.name(), lower.name());
        assertEquals(upper.name(), mixed.name());
    }

    @Test
    void factory_handles_whitespace() {
        ProductFactory factory = new ProductFactory();
        Product noSpace = factory.create("ESP+SHOT+OAT");
        Product withSpace = factory.create("ESP + SHOT + OAT");
        Product extraSpace = factory.create("  ESP  +  SHOT  +  OAT  ");
        assertEquals(noSpace.name(), withSpace.name());
        assertEquals(noSpace.name(), extraSpace.name());
        assertEquals(((Priced) noSpace).price(), ((Priced) withSpace).price());
    }
}