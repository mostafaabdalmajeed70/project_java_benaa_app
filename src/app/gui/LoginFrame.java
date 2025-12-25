package app.gui;

import app.data.UserDAO;
import app.models.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    private final Color ORANGE = new Color(255, 140, 0);
    private final Color DARK = new Color(40, 40, 40);

    public LoginFrame() {
        setTitle("Benaa | Login");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        // ===== Header =====
        JLabel title = new JLabel("Benaa", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(ORANGE);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setPreferredSize(new Dimension(100, 70));
        main.add(title, BorderLayout.NORTH);

        // ===== Form =====
        JPanel form = new JPanel(new GridLayout(5, 1, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        emailField = new JTextField();
        passwordField = new JPasswordField();

        form.add(labeled("Email", emailField));
        form.add(labeled("Password", passwordField));

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin());

        JButton registerBtn = new JButton("Register");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(ORANGE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        form.add(loginBtn);
        form.add(registerBtn);

        main.add(form, BorderLayout.CENTER);
        setContentPane(main);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(DARK);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleButton(JButton b) {
        b.setBackground(ORANGE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    // ================= LOGIN LOGIC =================

    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter email and password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        User user = UserDAO.login(email, pass);

        if (user == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid email or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // login done
        JOptionPane.showMessageDialog(
                this,
                "Welcome " + user.getName(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

        // ===== Role Based Navigation =====
        switch (user.getRole().toLowerCase()) {

            case "customer" -> new CustomerFrame(user);

            case "supplier" -> new SupplierFrame(user);

            case "worker"   -> new WorkerFrame(user);

            default -> JOptionPane.showMessageDialog(
                    this,
                    "Unknown role",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
