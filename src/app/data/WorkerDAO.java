package app.data;

import app.database.Database;
import app.models.Worker;

import java.sql.*;
import java.util.ArrayList;

public class WorkerDAO {

    public static ArrayList<Worker> getWorkersForCustomer(String location) {
        ArrayList<Worker> list = new ArrayList<>();

        if (location == null || location.isBlank()) {
            System.out.println("getWorkersForCustomer: location is null/empty");
            return list;
        }

        String sql = "SELECT * FROM users WHERE role='worker' AND location IS NOT NULL AND LOWER(location)=LOWER(?)";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, location.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Worker(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("specialization"),
                            rs.getDouble("rating"),
                            rs.getInt("experience"),
                            rs.getString("location")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("getWorkersForCustomer error: " + e.getMessage());
        }
        return list;
    }

    public static boolean updateProfile(int workerId, String name,
                                        String specialization, int experience) {

        String sql = """
            UPDATE users
            SET name = ?, specialization = ?, experience = ?
            WHERE id = ? AND role = 'worker'
        """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setInt(3, experience);
            ps.setInt(4, workerId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("updateProfile error: " + e.getMessage());
            return false;
        }
    }
}
