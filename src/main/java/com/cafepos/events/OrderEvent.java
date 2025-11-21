package com.cafepos.events;

public sealed interface OrderEvent permits OrderCreated, OrderPaid {}