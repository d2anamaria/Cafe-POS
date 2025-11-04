package com.cafepos.demo;
import com.cafepos.observer.CustomerNotifier;
import com.cafepos.observer.DeliveryDesk;
import com.cafepos.observer.KitchenDisplay;
import com.cafepos.order.*;
import com.cafepos.payment.*;
import com.cafepos.command.*;
import java.util.Scanner;

public final class Week8Demo_Commands {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println("=== Week 8 Command Pattern Demo ===");
            System.out.println("Choose demo mode:");
            System.out.println("1. Hardcoded demo");
            System.out.println("2. CLI demo");
            System.out.println("Q. Quit");

            System.out.print("Enter choice (1, 2, or Q): ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> runHardcodedDemo();
                case "2" -> runInteractiveDemo(scanner);
                case "Q", "QUIT" -> {
                    System.out.println("Goodbye!");
                    keepRunning = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }

    private static void runHardcodedDemo() {
        System.out.println("\n=== Running Hardcoded Demo ===");

        Order order = new Order(OrderIds.next());
//        order.register(new KitchenDisplay());
//        order.register(new CustomerNotifier());
//        order.register(new DeliveryDesk());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);
        remote.setSlot(0, new AddItemCommand(service,
                "ESP+SHOT+OAT", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L",
                2));
        remote.setSlot(2, new PayOrderCommand(service, new
                CardPayment("1234567890123456"), 10));
        remote.press(0);
        remote.press(1);
        remote.undo(); // remove last add
        remote.press(1); // add again
        remote.press(2); // pay

        System.out.println("=== Hardcoded Demo Complete ===");
    }

    private static void runInteractiveDemo(Scanner scanner) {
        System.out.println("\n=== Interactive Command Demo ===");

        Order order = new Order(OrderIds.next());
        order.register(new KitchenDisplay());
        order.register(new CustomerNotifier());
        order.register(new DeliveryDesk());

        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(5);

        remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 1));
        remote.setSlot(2, new AddItemCommand(service, "CAP+SHOT+OAT", 1));
        remote.setSlot(3, new PayOrderCommand(service, new CardPayment("1234567890123456"), 10));
        remote.setSlot(4, new PayOrderCommand(service, new CashPayment(), 10));

        System.out.println("\nPOS Remote configured with:");
        System.out.println("Slot 0: Add Espresso");
        System.out.println("Slot 1: Add Large Latte");
        System.out.println("Slot 2: Add Cappuccino + Extra Shot + Oat Milk");
        System.out.println("Slot 3: Pay with Card");
        System.out.println("Slot 4: Pay with Cash");

        boolean orderInProgress = true;
        boolean hasPaid = false;

        while (orderInProgress) {
            System.out.println("\nCommands: [0-4] Press button, [U]ndo, [V]iew order, [Q]uit");
            System.out.print("> ");
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "0", "1", "2" -> {
                    int slot = Integer.parseInt(input);
                    remote.press(slot);
                }
                case "3", "4" -> {
                    int slot = Integer.parseInt(input);
                    remote.press(slot);
                    hasPaid = true;
                    System.out.println("\n*** Order paid! Returning to main menu... ***");
                    orderInProgress = false;
                }
                case "U", "UNDO" -> remote.undo();
                case "V", "VIEW" -> viewOrder(order);
                case "Q", "QUIT" -> {
                    System.out.println("Demo cancelled!");
                    orderInProgress = false;
                }
                default -> System.out.println("Invalid command. Try again.");
            }
        }
    }

    private static void viewOrder(Order order) {
        System.out.println("\n=== Current Order ===");
        if (order.items().isEmpty()) {
            System.out.println("Order is empty.");
        } else {
            for (LineItem item : order.items()) {
                System.out.println("  - " + item.product().name() + " x" + item.quantity() + " = " + item.lineTotal());
            }
            System.out.println("Subtotal: " + order.subtotal());
            System.out.println("Total with tax (10%): " + order.totalWithTax(10));
        }
    }
}