package app.gui;
import app.data.PostDAO;
import app.data.WorkerRequestDAO;
import app.models.User;
import app.models.Worker;
import app.models.WorkerRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WorkerFrame extends JFrame {

    private final Worker worker;
    private JPanel contentPanel;

    private final Color ORANGE = new Color(255, 140, 0);
    private final Color DARK = new Color(45, 45, 45);

    public WorkerFrame(User user) {
        this.worker = (Worker) user; // safe cast (role already checked)

        setTitle("Benaa | Worker Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== Header =====
        JLabel header = new JLabel(
                "Welcome, " + worker.getName() + " | " + worker.getSpecialization(),
                SwingConstants.CENTER
        );
        header.setOpaque(true);
        header.setBackground(ORANGE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(100, 60));
        add(header, BorderLayout.NORTH);

        // ===== Sidebar =====
        JPanel sidebar = new JPanel(new GridLayout(4, 1, 0, 12));
        sidebar.setBackground(DARK);
        sidebar.setPreferredSize(new Dimension(200, 100));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));

        JButton jobsBtn   = sidebarButton("Requests");
        JButton postsBtn  = sidebarButton("Customer Posts");
        JButton profileBtn= sidebarButton("My Profile");
        JButton logoutBtn = sidebarButton("Logout");

        sidebar.add(jobsBtn);
        sidebar.add(postsBtn);
        sidebar.add(profileBtn);
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // ===== Content =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(centerLabel("Choose an option from the menu"));
        add(contentPanel, BorderLayout.CENTER);

        // ===== Actions =====
        jobsBtn.addActionListener(e -> showRequests());
        postsBtn.addActionListener(e -> showPosts());
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

    private void showRequests() {
        contentPanel.removeAll();

        String[] cols = {"Req ID", "CustomerID", "Message", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        var reqs = WorkerRequestDAO.getRequestsForWorker(worker.getUserID());
        for (WorkerRequest r : reqs) {
            model.addRow(new Object[]{r.id, r.customerId, r.message, r.status, r.createdAt});
        }

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton accept = new JButton("Accept");
        JButton reject = new JButton("Reject");

        accept.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row==-1) { JOptionPane.showMessageDialog(this,"Select request"); return; }
            int reqId = (int)table.getValueAt(row,0);
            WorkerRequestDAO.updateStatus(reqId,"ACCEPTED");
            showRequests();
        });

        reject.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row==-1) { JOptionPane.showMessageDialog(this,"Select request"); return; }
            int reqId = (int)table.getValueAt(row,0);
            WorkerRequestDAO.updateStatus(reqId,"REJECTED");
            showRequests();
        });

        JPanel bottom = new JPanel();
        bottom.add(accept);
        bottom.add(reject);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private void showPosts() {
        contentPanel.removeAll();

        DefaultListModel<String> model = new DefaultListModel<>();
        var posts = PostDAO.getPostsForWorker(worker.getLocation());
        for (var p : posts) {
            model.addElement("[" + p.createdAt + "] " + p.content + " (by user " + p.customerId + ")");
        }

        JList<String> list = new JList<>(model);
        JScrollPane scroll = new JScrollPane(list);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);

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

        panel.add(new JLabel("Name: " + worker.getName()));
        panel.add(new JLabel("Phone: " + worker.getPhone()));
        panel.add(new JLabel("Email: " + worker.getEmail()));
        panel.add(new JLabel("Specialization: " + worker.getSpecialization()));
        panel.add(new JLabel("Experience: " + worker.getExperience() + " years"));
        panel.add(new JLabel("Location: " + worker.getLocation()));

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
}
