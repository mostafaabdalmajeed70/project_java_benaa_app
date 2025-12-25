package app.data;

import app.database.Database;
import app.models.WorkerRequest;

import java.sql.*;
import java.util.ArrayList;

public class WorkerRequestDAO {

    public static boolean createRequest(int customerId, int workerId, String message) {
        String sql = "INSERT INTO worker_requests(customerId, workerId, message, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, workerId);
            ps.setString(3, message);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("createRequest error: " + e.getMessage());
            return false;
        }
    }

    public static ArrayList<WorkerRequest> getRequestsForWorker(int workerId) {
        ArrayList<WorkerRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM worker_requests WHERE workerId = ? ORDER BY createdAt DESC";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new WorkerRequest(
                            rs.getInt("id"),
                            rs.getInt("customerId"),
                            rs.getInt("workerId"),
                            rs.getString("message"),
                            rs.getString("status"),
                            rs.getString("createdAt")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("getRequestsForWorker error: " + e.getMessage());
        }
        return list;
    }

    public static boolean updateStatus(int requestId, String newStatus) {
        String sql = "UPDATE worker_requests SET status = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateStatus error: " + e.getMessage());
            return false;
        }
    }
}
