package com.cafepos.domain;

public final class OrderIds {
    private static long counter = 1;

    private OrderIds() {}

    public static long next() {
        return counter++;
    }
}
