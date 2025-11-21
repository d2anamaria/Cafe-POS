package com.cafepos.ui;

import com.cafepos.domain.*;
import com.cafepos.events.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.infra.Wiring;

import javax.swing.*;
import java.awt.*;

public final class PosSwingUI extends JFrame {
    private OrderController controller;
    private final EventBus bus;
    private long orderId;
    private Wiring.Components comp;

    private final DefaultListModel<String> cartModel = new DefaultListModel<>();
    private final JList<String> cartList = new JList<>(cartModel);
    private final JTextArea receiptArea = new JTextArea(12, 30);

    public PosSwingUI() {
        super("Café POS");
        this.comp = Wiring.createDefault();
        this.controller = new OrderController(comp.repo(), comp.checkout());
        this.bus = new EventBus();

        newOrder();
        initUI();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    private void newOrder() {
        this.orderId = OrderIds.next();
        controller.createOrder(orderId);
        setTitle("Café POS - Order #" + orderId);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel productPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        productPanel.setBorder(BorderFactory.createTitledBorder("Products"));

        String[][] products = {
                {"Espresso", "ESP"},
                {"Espresso + Shot", "ESP+SHOT"},
                {"Latte", "LAT"},
                {"Latte Large", "LAT+L"},
                {"Latte + Oat", "LAT+OAT"},
                {"Cappuccino", "CAP"},
                {"Cap + Syrup", "CAP+SYP"},
        };

        for (String[] p : products) {
            JButton btn = new JButton(p[0]);
            btn.addActionListener(e -> addItem(p[1]));
            productPanel.add(btn);
        }

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        cartList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        cartPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Receipt"));
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.addActionListener(e -> checkout());
        rightPanel.add(checkoutBtn, BorderLayout.SOUTH);

        add(productPanel, BorderLayout.WEST);
        add(cartPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void addItem(String recipe) {
        try {
            controller.addItem(orderId, recipe, 1);
            var product = new ProductFactory().create(recipe);
            cartModel.addElement(product.name() + " - " +
                    (product instanceof com.cafepos.decorator.Priced p ? p.price() : product.basePrice()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void checkout() {
        if (cartModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }
        String receipt = controller.checkout(orderId, 10);
        receiptArea.setText(receipt);

        cartModel.clear();
        newOrder();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PosSwingUI().setVisible(true));
    }
}