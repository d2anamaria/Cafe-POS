package com.cafepos.ui;

import com.cafepos.events.*;
import com.cafepos.domain.*;
import com.cafepos.events.EventBus;
import com.cafepos.factory.ProductFactory;
import com.cafepos.infra.Wiring;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class PosSwingUI extends JFrame {
    private final OrderController controller;
    private final EventBus bus;
    private final Wiring.Components comp;
    private long orderId;

    private final DefaultListModel<String> cartModel = new DefaultListModel<>();
    private final JTextArea receiptArea = new JTextArea(10, 25);
    private final JTextArea kitchenLog = new JTextArea(10, 25);
    private final JLabel lblStatus = new JLabel("STATE: NEW");
    private final JPanel productPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    private final JCheckBox chkVeg = new JCheckBox("Vegetarian Only");

    private final String[][] allProducts = {
            {"Espresso", "ESP"},
            {"Espresso + Shot", "ESP+SHOT"},
            {"Latte", "LAT"},
            {"Latte Large", "LAT+L"},
            {"Latte + Oat", "LAT+OAT"},
            {"Cappuccino", "CAP"},
            {"Cap + Syrup", "CAP+SYP"},
    };

    public PosSwingUI() {
        super("Café POS - Architectural Demo");
        this.comp = Wiring.createDefault();
        this.controller = new OrderController(comp.repo(), comp.checkout());
        this.bus = new EventBus();

        wireEventListeners();
        initUI();
        newOrder();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void wireEventListeners() {
        bus.on(OrderCreated.class, e -> {
            setTitle("Café POS - Order #" + e.orderId());
            lblStatus.setText("STATE: NEW");
            lblStatus.setForeground(Color.BLUE);
            logKitchen("New Order Started: #" + e.orderId());
        });

        bus.on(OrderPaid.class, e -> {
            lblStatus.setText("STATE: PAID / PREPARING");
            lblStatus.setForeground(new Color(0, 128, 0));
            String receipt = controller.checkout(e.orderId(), 10);
            receiptArea.setText(receipt);

            logKitchen("$$ Order #" + e.orderId() + " PAID. Preparing items...");
        });
    }

    private void newOrder() {
        this.orderId = OrderIds.next();
        controller.createOrder(orderId);
        cartModel.clear();
        receiptArea.setText("");
        bus.emit(new OrderCreated(orderId));
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel leftContainer = new JPanel(new BorderLayout(5, 5));
        leftContainer.setBorder(BorderFactory.createTitledBorder("Menu (Composite/Iterator)"));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkVeg.addActionListener(e -> renderProductButtons());
        filterPanel.add(chkVeg);

        renderProductButtons();

        leftContainer.add(filterPanel, BorderLayout.NORTH);
        leftContainer.add(new JScrollPane(productPanel), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Current Order"));

        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(lblStatus, BorderLayout.NORTH);

        centerPanel.add(new JScrollPane(new JList<>(cartModel)), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton btnUndo = new JButton("Undo Last");
        btnUndo.addActionListener(e -> performUndo());
        JButton btnCheckout = new JButton("Pay & Checkout");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 12));
        btnCheckout.setBackground(new Color(29, 226, 29));
        btnCheckout.addActionListener(e -> checkout());

        controls.add(btnUndo);
        controls.add(btnCheckout);
        centerPanel.add(controls, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel pnlReceipt = new JPanel(new BorderLayout());
        pnlReceipt.setBorder(BorderFactory.createTitledBorder("Customer Receipt"));
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pnlReceipt.add(new JScrollPane(receiptArea));

        JPanel pnlKitchen = new JPanel(new BorderLayout());
        pnlKitchen.setBorder(BorderFactory.createTitledBorder("Kitchen Display (Observer)"));
        pnlKitchen.setBackground(Color.BLACK);
        kitchenLog.setBackground(Color.BLACK);
        kitchenLog.setForeground(Color.GREEN);
        kitchenLog.setFont(new Font("Monospaced", Font.BOLD, 12));
        pnlKitchen.add(new JScrollPane(kitchenLog));

        rightPanel.add(pnlReceipt);
        rightPanel.add(pnlKitchen);

        add(leftContainer, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }


    private void renderProductButtons() {
        productPanel.removeAll();
        boolean vegOnly = chkVeg.isSelected();

        for (String[] p : allProducts) {
            boolean isVeg = p[0].contains("Oat") || p[0].contains("Espresso");

            if (vegOnly && !isVeg) continue;

            JButton btn = new JButton("<html><center>"+p[0]+"</center></html>");
            btn.addActionListener(e -> addItem(p[1]));
            productPanel.add(btn);
        }
        productPanel.revalidate();
        productPanel.repaint();
    }

    private void addItem(String recipe) {
        try {
            controller.addItem(orderId, recipe, 1);

            var product = new ProductFactory().create(recipe);
            cartModel.addElement(product.name());

            logKitchen(" + PREP: " + product.name());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void performUndo() {
        if (!cartModel.isEmpty()) {
            String removed = cartModel.remove(cartModel.getSize() - 1);
            logKitchen(" ! CANCEL ITEM: " + removed);
        }
    }

    private void checkout() {
        if (cartModel.isEmpty()) return;

        bus.emit(new OrderPaid(orderId));

        Timer t = new Timer(4000, e -> newOrder());
        t.setRepeats(false);
        t.start();
    }

    private void logKitchen(String msg) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        kitchenLog.append("[" + time + "] " + msg + "\n");
        kitchenLog.setCaretPosition(kitchenLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new PosSwingUI().setVisible(true));
    }
}