package com.cafepos.demo;

import com.cafepos.state.OrderFSM;
import java.util.Scanner;

public final class Week9Demo_State {
    public static void main(String[] args) {

        System.out.println("=== Automated Demo ===");
        OrderFSM fsm = new OrderFSM();
        System.out.println("Status = " + fsm.status());
        fsm.prepare();
        fsm.pay();
        fsm.prepare();
        fsm.markReady();
        fsm.deliver();
        System.out.println("Status = " + fsm.status());

        System.out.println("\n=== Interactive Demo ===");
        OrderFSM interactiveFsm = new OrderFSM();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nCurrent Status: " + interactiveFsm.status());

            if (interactiveFsm.status().equals("DELIVERED") || interactiveFsm.status().equals("CANCELLED")) {
                System.out.println("Order reached terminal state. Exiting...");
                break;
            }

            System.out.println("Commands: pay | prepare | markReady | deliver | cancel | exit");
            System.out.print("> ");

            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "pay" -> interactiveFsm.pay();
                case "prepare" -> interactiveFsm.prepare();
                case "markready" -> interactiveFsm.markReady();
                case "deliver" -> interactiveFsm.deliver();
                case "cancel" -> interactiveFsm.cancel();
                case "exit" -> {
                    System.out.println("Exiting...");
                    running = false;
                }
                default -> System.out.println("Unknown command: " + command);
            }
        }

        scanner.close();
    }
}