package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MoneyTests {
    @Test
    public void testMoneyAddition() {
        Money m1 = Money.of(16.50);
        Money m2 = Money.of(3.50);
        Money sum = m1.add(m2);
        assertEquals(Money.of(20.00), sum);
    }

    @Test
    public void testMoneyMultiplication() {
        Money m = Money.of(12.50);
        Money result = m.multiply(3);

        assertEquals(Money.of(37.50), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoneyMultiplicationWithNegativeValue() {
        Money m = Money.of(2.50);
        m.multiply(-3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoneyCreationWithNegativeValue() {
        Money.of(-5.00);
    }
}
