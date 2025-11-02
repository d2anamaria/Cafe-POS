package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.order.LineItem;
import com.cafepos.order.Order;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;


public class PaymentTests {
    @Test
    public void payment_strategy_called() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;
        order.pay(fake);
        assertTrue("Payment strategy should be called",called[0]);
    }

    @Test
    public void cash_payment_prints_correct_message() {
        var p=new SimpleProduct("A", "A", Money.of(10));
        var order=new Order(1);
        order.addItem(new LineItem(p,3));
        var payment=new CashPayment();

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        order.pay(payment);

        String printed=out.toString().trim();
        assertTrue( "Should mention cash payment",printed.contains("[Cash]"));
        assertTrue("Should include total price",printed.contains("33"));
    }

    @Test
    public void card_payment_masks_card_number() {
        var p=new SimpleProduct("A","A",Money.of(5));
        var order=new Order(1);
        order.addItem(new LineItem(p,2));
        var payment =new CardPayment("1234567812341234");

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        order.pay(payment);

        String printed=out.toString().trim();
        assertTrue("Should show only last 4 digits",printed.contains("****1234"));
        assertFalse("Should not reveal full card number",printed.contains("12345678"));
    }

    @Test
    public void wallet_payment_prints_wallet_id() {
        var p=new SimpleProduct("A", "A", Money.of(5));
        var order=new Order(1);
        order.addItem(new LineItem(p, 2));
        var payment=new WalletPayment("alice-wallet-01");

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        order.pay(payment);

        String printed=out.toString().trim();
        assertTrue("Should include wallet id in message",printed.contains("alice-wallet-01"));
    }

    @Test
    public void pay_throws_if_strategy_null() {
        var p=new SimpleProduct("A","A",Money.of(5));
        var order=new Order(1);
        order.addItem(new LineItem(p,2));

        assertThrows(IllegalArgumentException.class, ()->order.pay(null),"Null strategy not allowed");
    }
}
