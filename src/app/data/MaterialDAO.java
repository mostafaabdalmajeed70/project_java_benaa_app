package app.data;

import app.database.Database;
import app.models.Material;

import java.sql.*;
import java.util.ArrayList;

public class MaterialDAO {

    public static int insertMaterial(Material m) {
        String sql = "INSERT INTO materials(name, price, quantity, supplierID) VALUES (?,?,?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getName());
            ps.setDouble(2, m.getPrice());
            ps.setInt(3, m.getQuantity());
            ps.setInt(4, m.getSupplierID());
            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("insertMaterial error: " + e.getMessage());
        }
        return -1;
    }

    public static ArrayList<Material> getMaterialsForCustomer(String customerLocation) {

        ArrayList<Material> list = new ArrayList<>();

        if (customerLocation == null || customerLocation.isBlank()) {
            System.out.println("getMaterialsForCustomer: customerLocation is null/empty");
            return list;
        }

        String sql = """
            SELECT m.*
            FROM materials m
            JOIN users u ON m.supplierID = u.id
            WHERE (u.shopLocation IS NOT NULL AND LOWER(u.shopLocation) = LOWER(?))
               OR IFNULL(u.delivery,0) = 1
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerLocation.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Material(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getInt("supplierID")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println("getMaterialsForCustomer error: " + e.getMessage());
        }

        return list;
    }

    public static Material getById(int id) {
        String sql = "SELECT * FROM materials WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Material(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getInt("supplierID")
                );
            }
        } catch (SQLException e) {
            System.out.println("getById error: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateQuantity(Connection conn, int id, int newQty) {
        String sql = "UPDATE materials SET quantity=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateQuantity error: " + e.getMessage());
            return false;
        }
    }

    public static ArrayList<Material> getMaterialsBySupplierId(int supplierId) {
        ArrayList<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE supplierID=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Material(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getInt("supplierID")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("getMaterialsBySupplierId error: " + e.getMessage());
        }
        return list;
    }

    public static boolean updateMaterial(int id, double price, int qty) {
        String sql = "UPDATE materials SET price=?, quantity=? WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, qty);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("updateMaterial error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteMaterial(int id) {
        String sql = "DELETE FROM materials WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("deleteMaterial error: " + e.getMessage());
            return false;
        }
    }
}
