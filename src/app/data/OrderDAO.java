package app.data;

import app.database.Database;
import app.models.*;

import java.sql.*;
import java.util.ArrayList;

public class OrderDAO {

    public static int createOrder(int customerId, int materialId, int qty) {
        String sqlMaterial = "SELECT price, quantity FROM materials WHERE id=?";
        String sqlOrder = "INSERT INTO orders(customerID, materialID, quantity, total_price, status) VALUES (?,?,?,?, 'PENDING')";

        try (Connection conn = Database.connect()) {

            conn.setAutoCommit(false); // start Transaction

            try (PreparedStatement mps = conn.prepareStatement(sqlMaterial)) {

                mps.setInt(1, materialId);
                try (ResultSet rs = mps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return -1;
                    }

                    double price = rs.getDouble("price");
                    int available = rs.getInt("quantity");
                    if (qty <= 0 || qty > available) {
                        conn.rollback();
                        return -1;
                    }

                    double total = price * qty;

                    try (PreparedStatement ops = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

                        ops.setInt(1, customerId);
                        ops.setInt(2, materialId);
                        ops.setInt(3, qty);
                        ops.setDouble(4, total);
                        ops.executeUpdate();

                        // update quantity using same connection
                        boolean ok = MaterialDAO.updateQuantity(conn, materialId, available - qty);
                        if (!ok) {
                            conn.rollback();
                            return -1;
                        }

                        try (ResultSet gk = ops.getGeneratedKeys()) {
                            if (!gk.next()) {
                                conn.rollback();
                                return -1;
                            }

                            int orderId = gk.getInt(1);
                            conn.commit();
                            return orderId;
                        }
                    }
                }

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("createOrder error: " + e.getMessage());
        }

        return -1;
    }

    public static ArrayList<Order> getOrdersForSupplier(int supplierId) {
        ArrayList<Order> list = new ArrayList<>();
        String sql = "SELECT o.id AS oid, o.customerID, o.materialID, o.quantity, o.status " +
                "FROM orders o JOIN materials m ON o.materialID = m.id WHERE m.supplierID=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int oid = rs.getInt("oid");
                    int custId = rs.getInt("customerID");
                    int mid = rs.getInt("materialID");
                    int qty = rs.getInt("quantity");
                    String status = rs.getString("status");
                    Material mat = MaterialDAO.getById(mid);
                    User user = UserDAO.loginById(custId);
                    Customer cust = null;
                    if (user instanceof Customer) cust = (Customer) user;
                    Order o = new Order(oid, cust, mat, qty, status);
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            System.out.println("getOrdersForSupplier error: " + e.getMessage());
        }
        return list;
    }

    public static ArrayList<Object[]> getOrdersTableForSupplier(int supplierId) {

        ArrayList<Object[]> rows = new ArrayList<>();

        String sql = """
        SELECT o.id,
               u.name AS customer,
               m.name AS material,
               o.quantity,
               o.status
        FROM orders o
        JOIN materials m ON o.materialID = m.id
        JOIN users u ON o.customerID = u.id
        WHERE m.supplierID = ?
        ORDER BY o.id DESC
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("customer"),
                        rs.getString("material"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            System.out.println("getOrdersTableForSupplier error: " + e.getMessage());
        }

        return rows;
    }

    public static boolean updateStatus(int orderId, String status) {

        String sql = "UPDATE orders SET status=? WHERE id=?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static ArrayList<Object[]> getOrdersForCustomer(int customerId) {

        ArrayList<Object[]> rows = new ArrayList<>();

        String sql = """
        SELECT o.id,
               m.name AS material,
               o.quantity,
               o.total_price,
               o.status
        FROM orders o
        JOIN materials m ON o.materialID = m.id
        WHERE o.customerID = ?
        ORDER BY o.id DESC
    """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("material"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            System.out.println("getOrdersForCustomer error: " + e.getMessage());
        }

        return rows;
    }

}
