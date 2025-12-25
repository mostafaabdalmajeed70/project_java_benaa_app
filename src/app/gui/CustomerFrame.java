package app.gui;

import app.data.WorkerDAO;
import app.data.MaterialDAO;
import app.data.OrderDAO;
import app.data.PostDAO;
import app.data.WorkerRequestDAO;
import app.models.User;
import app.models.Material;
import app.models.Worker;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class CustomerFrame extends JFrame {

    private final User customer;
    private JPanel contentPanel;

    private final Color ORANGE = new Color(255, 140, 0);
    private final Color DARK = new Color(45, 45, 45);

    public CustomerFrame(User customer) {
        this.customer = customer;

        setTitle("Benaa | Customer Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== Header =====
        JLabel header = new JLabel("Welcome, " + customer.getName(), SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(ORANGE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setPreferredSize(new Dimension(100, 60));
        add(header, BorderLayout.NORTH);

        // ===== Sidebar =====
        JPanel sidebar = new JPanel(new GridLayout(5, 1, 0, 10));
        sidebar.setBackground(DARK);
        sidebar.setPreferredSize(new Dimension(180, 100));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton materialsBtn = sidebarButton("Materials");
        JButton workersBtn = sidebarButton("Workers");
        JButton postBtn = sidebarButton("New Post");
        JButton ordersBtn = sidebarButton("My Orders");
        JButton logoutBtn = sidebarButton("Logout");

        sidebar.add(materialsBtn);
        sidebar.add(workersBtn);
        sidebar.add(postBtn);
        sidebar.add(ordersBtn);
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // ===== Content =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(centerLabel("Choose an option from the menu"));
        add(contentPanel, BorderLayout.CENTER);

        // ===== Actions =====
        materialsBtn.addActionListener(e -> showMaterials());
        workersBtn.addActionListener(e -> showWorkers());
        postBtn.addActionListener(e -> showNewPost());
        ordersBtn.addActionListener(e -> showOrders());
        logoutBtn.addActionListener(e -> logout());
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(DARK);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createLineBorder(ORANGE));
        return btn;
    }

    private JLabel centerLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return label;
    }

    // ================= MATERIALS =================

    private void showMaterials() {
        contentPanel.removeAll();

        String[] cols = {"ID", "Name", "Price", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String location = customer.getLocationSafe();

        if (location == null || location.isBlank()) {
            location = JOptionPane.showInputDialog(
                    this,
                    "Enter your location to see nearby suppliers:"
            );
            if (location == null || location.isBlank()) return;
        }

        ArrayList<Material> materials = MaterialDAO.getMaterialsForCustomer(location);

        for (Material m : materials) {
            model.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getPrice(),
                    m.getQuantity()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(table);

        JButton orderBtn = new JButton("Place Order");
        orderBtn.setBackground(ORANGE);
        orderBtn.setForeground(Color.WHITE);

        orderBtn.addActionListener(e -> placeOrder(table));

        JPanel bottom = new JPanel();
        bottom.add(orderBtn);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private void placeOrder(JTable table) {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a material first");
            return;
        }

        int materialId = (int) table.getValueAt(row, 0);
        String name = table.getValueAt(row, 1).toString();
        int available = Integer.parseInt(table.getValueAt(row, 3).toString());

        JTextField qtyField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Material: " + name));
        panel.add(new JLabel("Available: " + available));
        panel.add(new JLabel("Quantity"));
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Place Order",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0 || qty > available)
                throw new Exception();

            int orderId = OrderDAO.createOrder(
                    customer.getUserID(),
                    materialId,
                    qty
            );

            if (orderId == -1) {
                JOptionPane.showMessageDialog(this, "Order failed");
            } else {
                JOptionPane.showMessageDialog(this, "Order placed successfully");
                showMaterials();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
        }
    }

    // ================= OTHER SECTIONS =================

    private void showWorkers() {

        contentPanel.removeAll();

        String[] cols = {
                "ID", "Name", "Specialization", "Experience", "Rating"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0);

        var workers = WorkerDAO.getWorkersForCustomer(customer.getLocationSafe());

        for (Worker w : workers) {
            model.addRow(new Object[]{
                    w.getUserID(),
                    w.getName(),
                    w.getSpecialization(),
                    w.getExperience() + " years",
                    w.getRating()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(table);

        JButton requestBtn = new JButton("Request Worker");
        requestBtn.setBackground(ORANGE);
        requestBtn.setForeground(Color.WHITE);

        requestBtn.addActionListener(e -> requestWorker(table));

        JPanel bottom = new JPanel();
        bottom.add(requestBtn);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private void requestWorker(JTable table) {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a worker first");
            return;
        }

        int workerId = (int) table.getValueAt(row, 0);
        String workerName = table.getValueAt(row, 1).toString();

        String msg = JOptionPane.showInputDialog(this, "Enter a short message for " + workerName + ":");
        if (msg == null) return;

        boolean ok = WorkerRequestDAO.createRequest(customer.getUserID(), workerId, msg);
        if (ok) JOptionPane.showMessageDialog(this, "Request sent to " + workerName);
        else JOptionPane.showMessageDialog(this, "Failed to send request");
    }

    private void showNewPost() {
        contentPanel.removeAll();

        JTextArea area = new JTextArea();
        area.setLineWrap(true);

        JButton send = new JButton("Post");
        send.setBackground(ORANGE);
        send.setForeground(Color.WHITE);

        send.addActionListener(e -> {
            String text = area.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Post cannot be empty");
                return;
            }

            boolean ok = PostDAO.addPost(customer.getUserID(), customer.getLocationSafe(), text);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Post published ✔");
                area.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to publish");
            }
        });

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.add(send);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private void showOrders() {
        contentPanel.removeAll();

        String[] cols = {"Order ID", "Material", "Qty", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        var rows = OrderDAO.getOrdersForCustomer(customer.getUserID());
        for (Object[] r : rows) model.addRow(r);

        JTable table = new JTable(model);
        table.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(table);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
