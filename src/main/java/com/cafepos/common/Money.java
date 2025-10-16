package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
public final class Money implements Comparable<Money> {
    private final BigDecimal amount;

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");
        if (a.compareTo(BigDecimal.ZERO)<0) throw new IllegalArgumentException("positive amount required");
        this.amount = a.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(double qty) {
        if (qty<0) throw new IllegalArgumentException("Cannot multiply money with a negative number");
        return new Money(this.amount.multiply(new BigDecimal(qty)));
    }

    // equals, hashCode, toString, etc
    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(!(obj instanceof Money)) return false;
        Money other = (Money) obj;
        return amount.compareTo(other.amount)==0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public String toString() {
        return amount.toString();
    }
    public BigDecimal asBigDecimal() {
        return amount;
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }
}