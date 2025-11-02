package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.order.LineItem;
import com.cafepos.order.Order;
import com.cafepos.order.OrderIds;
import com.cafepos.factory.ProductFactory;

import java.util.Scanner;

public final class Week5Demo {
    private static final ProductFactory factory = new ProductFactory();
    private static Order currentOrder = new Order(OrderIds.next());
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Café POS System ===");
        System.out.println("Available base drinks: ESP (Espresso), LAT (Latte), CAP (Cappuccino)");
        System.out.println("Available add-ons: SHOT (Extra Shot), OAT (Oat Milk), SYP (Syrup), L (Large)");
        System.out.println("Example: ESP+SHOT+OAT+L");
        System.out.println();

        boolean running = true;
        while (running) {
            System.out.println("\nCommands: [A]dd item, [V]iew cart, [C]heckout, [Q]uit");
            System.out.print("> ");

            String command = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case "A", "ADD" -> addItem();
                case "V", "VIEW" -> viewCart();
                case "C", "CHECKOUT" -> {
                    checkout();
                    running = false;
                }
                case "Q", "QUIT" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Unknown command. Try again.");
            }
        }

        scanner.close();
    }

    private static void addItem() {
        System.out.print("Enter recipe (e.g., ESP+SHOT+OAT): ");
        String recipe = scanner.nextLine().trim();

        if (recipe.isEmpty()) {
            System.out.println("Recipe cannot be empty.");
            return;
        }

        try {
            Product product = factory.create(recipe);

            System.out.print("Enter quantity: ");
            String qtyInput = scanner.nextLine().trim();
            int quantity = Integer.parseInt(qtyInput);


            currentOrder.addItem(new LineItem(product, quantity));
            System.out.println("✓ Added: " + product.name() + " x" + quantity);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewCart() {
        if (currentOrder.items().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\n=== Current Cart ===");
        for (LineItem li : currentOrder.items()) {
            System.out.println("  - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }
        System.out.println("\nSubtotal: " + currentOrder.subtotal());
    }

    private static void checkout() {
        if (currentOrder.items().isEmpty()) {
            System.out.println("Cannot checkout with empty cart.");
            return;
        }

        System.out.println("\n=== RECEIPT ===");
        System.out.println("Order #" + currentOrder.id());

        for (LineItem li : currentOrder.items()) {
            System.out.println("  - " + li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }

        System.out.println("\nSubtotal: " + currentOrder.subtotal());
        System.out.println("Tax (10%): " + currentOrder.taxAtPercent(10));
        System.out.println("Total: " + currentOrder.totalWithTax(10));
        System.out.println("\nThank you for your order!");
    }
}