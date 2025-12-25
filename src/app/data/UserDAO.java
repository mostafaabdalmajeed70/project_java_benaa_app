package app.data;

import app.database.Database;
import app.models.*;

import java.sql.*;

public class UserDAO {

    // insert user and return generated id (or -1 on failure)
    public static int insertUser(User u, String password) {
        String sql = "INSERT INTO users(name, phone, email, password, role, specialization, experience, rating, location, shopLocation, delivery) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.name);
            ps.setString(2, (u instanceof Customer) ? ((Customer) u).getPhoneSafe() : u.getPhone());
            ps.setString(3, u.getEmail());
            ps.setString(4, password);
            ps.setString(5, u.role);

            // specialization / experience / rating / location / shopLocation / delivery
            if (u instanceof Worker) {
                Worker w = (Worker) u;
                ps.setString(6, w.specialization);
                ps.setInt(7, w.experience);
                ps.setDouble(8, w.rating);
                ps.setString(9, w.getLocation());
                ps.setString(10, null);
                ps.setInt(11, 0);
            } else if (u instanceof Supplier) {
                Supplier s = (Supplier) u;
                ps.setString(6, null);
                ps.setInt(7, 0);
                ps.setDouble(8, 0.0);
                ps.setString(9, null);
                ps.setString(10, s.getShopLocation());
                ps.setInt(11, s.hasDelivery() ? 1 : 0);
            } else if (u instanceof Customer) {
                Customer c = (Customer) u;
                ps.setString(6, null);
                ps.setInt(7, 0);
                ps.setDouble(8, 0.0);
                ps.setString(9, c.getPreferredLocation());
                ps.setString(10, null);
                ps.setInt(11, 0);
            } else {
                ps.setString(6, null);
                ps.setInt(7, 0);
                ps.setDouble(8, 0.0);
                ps.setString(9, null);
                ps.setString(10, null);
                ps.setInt(11, 0);
            }

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("InsertUser error: " + e.getMessage());
        }
        return -1;
    }

    // login by email + password -> return a model instance typed by role
    public static User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String role = rs.getString("role");

                if ("customer".equalsIgnoreCase(role)) {
                    String location = rs.getString("location");
                    Customer c = new Customer(id, name, phone, email, location == null ? "" : location);
                    return c;
                } else if ("supplier".equalsIgnoreCase(role)) {
                    String shop = rs.getString("shopLocation");
                    boolean delivery = rs.getInt("delivery") == 1;
                    Supplier s = new Supplier(id, name, phone, email, shop == null ? "" : shop, delivery);
                    return s;
                } else if ("worker".equalsIgnoreCase(role)) {
                    String spec = rs.getString("specialization");
                    int exp = rs.getInt("experience");
                    double rating = rs.getDouble("rating");
                    String loc = rs.getString("location");
                    Worker w = new Worker(id, name, phone, email, spec == null ? "" : spec, rating, exp, loc == null ? "" : loc);
                    return w;
                } else {
                    // generic fallback
                    return new User(id, name, phone, email, role);
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    // helper: find supplier by id (returns Supplier or null)
    public static Supplier findSupplierById(int id) {
        String sql = "SELECT * FROM users WHERE id=? AND role='supplier'";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("shopLocation"),
                        rs.getInt("delivery") == 1
                );
            }
        } catch (SQLException e) {
            System.out.println("findSupplierById error: " + e.getMessage());
        }
        return null;
    }

    // helper to load user by id (returns User or subclass)
    public static User loginById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String role = rs.getString("role");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                if ("customer".equalsIgnoreCase(role)) {
                    return new Customer(rs.getInt("id"), name, phone, email, rs.getString("location"));
                } else if ("supplier".equalsIgnoreCase(role)) {
                    return new Supplier(rs.getInt("id"), name, phone, email, rs.getString("shopLocation"), rs.getInt("delivery")==1);
                } else if ("worker".equalsIgnoreCase(role)) {
                    return new Worker(rs.getInt("id"), name, phone, email, rs.getString("specialization"), rs.getDouble("rating"), rs.getInt("experience"), rs.getString("location"));
                } else {
                    return new User(rs.getInt("id"), name, phone, email, role);
                }
            }
        } catch (SQLException e) {
            System.out.println("loginById error: " + e.getMessage());
        }
        return null;
    }

}
