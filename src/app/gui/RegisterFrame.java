package app.gui;

import app.data.UserDAO;
import app.models.*;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField nameField, phoneField, emailField, locationField, specField, expField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    private JCheckBox deliveryBox;

    private final Color ORANGE = new Color(255, 140, 0);

    public RegisterFrame() {
        setTitle("Benaa | Register");
        setSize(450, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(ORANGE);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setPreferredSize(new Dimension(100, 60));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        form.setBackground(Color.WHITE);

        roleBox = new JComboBox<>(new String[]{"Customer", "Supplier", "Worker"});
        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        passField = new JPasswordField();
        locationField = new JTextField();
        specField = new JTextField();
        expField = new JTextField();
        deliveryBox = new JCheckBox("Delivery Available");

        form.add(labeled("User Type", roleBox));
        form.add(labeled("Name", nameField));
        form.add(labeled("Phone", phoneField));
        form.add(labeled("Email", emailField));
        form.add(labeled("Password", passField));
        form.add(labeled("Location", locationField));
        form.add(labeled("Specialization (Worker)", specField));
        form.add(labeled("Experience (years)", expField));
        form.add(deliveryBox);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(ORANGE);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.addActionListener(e -> handleRegister());

        JButton backBtn = new JButton("Back to Login");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(ORANGE);
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        form.add(registerBtn);
        form.add(backBtn);

        main.add(form, BorderLayout.CENTER);
        setContentPane(main);

        roleBox.addActionListener(e -> updateFields());
        updateFields();
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void updateFields() {
        String role = roleBox.getSelectedItem().toString();

        specField.setEnabled(role.equals("Worker"));
        expField.setEnabled(role.equals("Worker"));
        deliveryBox.setEnabled(role.equals("Supplier"));
    }

    private void handleRegister() {
        String role = roleBox.getSelectedItem().toString().toLowerCase();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = null;

        try {
            switch (role) {
                case "customer" -> user = new Customer(0, name, phone, email, location);
                case "supplier" -> user = new Supplier(0, name, phone, email, location, deliveryBox.isSelected());
                case "worker" -> {
                    int exp = Integer.parseInt(expField.getText().trim().isEmpty() ? "0" : expField.getText().trim());
                    user = new Worker(0, name, phone, email, specField.getText().trim(), 0.0, exp, location);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = UserDAO.insertUser(user, pass);

        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Account created successfully ✔");
            dispose();
            new LoginFrame();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed (email exists?)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
