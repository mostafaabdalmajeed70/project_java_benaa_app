package app.gui;
import app.data.OrderDAO;
import app.models.Supplier;
import app.models.User;
import app.data.MaterialDAO;
import app.models.Material;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

public class SupplierFrame extends JFrame {

    private final Supplier supplier;
    private JPanel contentPanel;

    private final Color ORANGE = new Color(255, 140, 0);
    private final Color DARK = new Color(45, 45, 45);

    public SupplierFrame(User user) {
        this.supplier = (Supplier) user; // safe cast

        setTitle("Benaa | Supplier Dashboard");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== Header =====
        JLabel header = new JLabel(
                "Welcome, " + supplier.getName(),
                SwingConstants.CENTER
        );
        header.setOpaque(true);
        header.setBackground(ORANGE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setPreferredSize(new Dimension(100, 60));
        add(header, BorderLayout.NORTH);

        // ===== Sidebar =====
        JPanel sidebar = new JPanel(new GridLayout(5, 1, 0, 12));
        sidebar.setBackground(DARK);
        sidebar.setPreferredSize(new Dimension(200, 100));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));

        JButton materialsBtn = sidebarButton("My Materials");
        JButton addBtn       = sidebarButton("Add Material");
        JButton ordersBtn    = sidebarButton("Orders");
        JButton profileBtn   = sidebarButton("My Profile");
        JButton logoutBtn    = sidebarButton("Logout");

        sidebar.add(materialsBtn);
        sidebar.add(addBtn);
        sidebar.add(ordersBtn);
        sidebar.add(profileBtn);
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // ===== Content =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(centerLabel("Choose an option from the menu"));
        add(contentPanel, BorderLayout.CENTER);

        // ===== Actions =====
        materialsBtn.addActionListener(e -> showMaterials());
        addBtn.addActionListener(e -> showAddMaterial());
        ordersBtn.addActionListener(e -> showOrders());
        profileBtn.addActionListener(e -> showProfile());
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

    // ================= Sections =================

    private void showMaterials() {

        String[] cols = {"ID", "Name", "Price", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        ArrayList<Material> materials =
                MaterialDAO.getMaterialsBySupplierId(supplier.getUserID());

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

        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        styleButton(editBtn);
        styleButton(deleteBtn);

        JPanel bottom = new JPanel();
        bottom.add(editBtn);
        bottom.add(deleteBtn);

        editBtn.addActionListener(e -> editMaterial(table));
        deleteBtn.addActionListener(e -> deleteMaterial(table));

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);
        refresh();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void showAddMaterial() {

        JTextField nameField  = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField   = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Material Name"));
        panel.add(nameField);
        panel.add(new JLabel("Price"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity"));
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Material",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());

            if (name.isEmpty() || price <= 0 || qty <= 0)
                throw new IllegalArgumentException();

            Material m = new Material(0, name, price, qty, supplier.getUserID());
            MaterialDAO.insertMaterial(m);

            showMaterials(); // refresh table

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid input",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void editMaterial(JTable table) {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a material first");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String name = table.getValueAt(row, 1).toString();
        double price = Double.parseDouble(table.getValueAt(row, 2).toString());
        int qty = Integer.parseInt(table.getValueAt(row, 3).toString());

        JTextField priceField = new JTextField(String.valueOf(price));
        JTextField qtyField   = new JTextField(String.valueOf(qty));

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Material: " + name));
        panel.add(new JLabel("Price"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity"));
        panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Material",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            double newPrice = Double.parseDouble(priceField.getText().trim());
            int newQty = Integer.parseInt(qtyField.getText().trim());

            if (newPrice <= 0 || newQty < 0)
                throw new IllegalArgumentException();

            MaterialDAO.updateMaterial(id, newPrice, newQty);
            showMaterials();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }

    private void deleteMaterial(JTable table) {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a material first");
            return;
        }

        int id = (int) table.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this material?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        MaterialDAO.deleteMaterial(id);
        showMaterials();
    }

    private void showOrders() {

        String[] cols = {
                "ID", "Customer", "Material", "Qty", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0);

        var rows = OrderDAO.getOrdersTableForSupplier(supplier.getUserID());
        for (Object[] r : rows) model.addRow(r);

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton approve = new JButton("Approve");
        JButton reject  = new JButton("Reject");
        JButton done    = new JButton("Done");

        approve.addActionListener(e -> changeStatus(table, "APPROVED"));
        reject.addActionListener(e -> changeStatus(table, "REJECTED"));
        done.addActionListener(e -> changeStatus(table, "DONE"));

        JPanel actions = new JPanel();
        actions.add(approve);
        actions.add(reject);
        actions.add(done);

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(actions, BorderLayout.SOUTH);

        refresh();
    }

    private void showProfile() {
        contentPanel.removeAll();
        contentPanel.add(profilePanel());
        refresh();
    }

    private JPanel profilePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        panel.add(new JLabel("Name: " + supplier.getName()));
        panel.add(new JLabel("Phone: " + supplier.getPhone()));
        panel.add(new JLabel("Email: " + supplier.getEmail()));
        panel.add(new JLabel("Location: " + supplier.getLocation()));
        panel.add(new JLabel("Delivery Available: " +
                (supplier.hasDelivery() ? "Yes" : "No")));

        return panel;
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }

    private void changeStatus(JTable table, String newStatus) {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int orderId = Integer.parseInt(
                table.getValueAt(row, 0).toString()
        );

        String current =
                table.getValueAt(row, 4).toString();

        if (current.equals("REJECTED") || current.equals("DONE")) {
            JOptionPane.showMessageDialog(this,
                    "Order already closed");
            return;
        }

        if (OrderDAO.updateStatus(orderId, newStatus)) {
            showOrders();
        }
    }

}
