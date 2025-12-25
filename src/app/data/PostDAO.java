package app.data;

import app.database.Database;
import app.models.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public static boolean addPost(int customerId, String content, String location) {

        if (location == null || location.isBlank()) {
            System.out.println("Post location is missing");
            return false;
        }

        String sql = "INSERT INTO posts(customerId, content, location) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setString(2, content);
            ps.setString(3, location.trim());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add post error: " + e.getMessage());
            return false;
        }
    }


    public static List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY createdAt DESC";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("id"),
                        rs.getInt("customerId"),
                        rs.getString("location"),
                        rs.getString("content"),
                        rs.getString("createdAt")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Get posts error: " + e.getMessage());
        }
        return posts;
    }

    public static List<Post> getPostsForWorker(String workerLocation) {

        List<Post> posts = new ArrayList<>();

        String sql = """
        SELECT * FROM posts
        WHERE LOWER(location) = LOWER(?)
        ORDER BY createdAt DESC
    """;

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, workerLocation);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("id"),
                        rs.getInt("customerId"),
                        rs.getString("content"),
                        rs.getString("location"),
                        rs.getString("createdAt")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Get posts error: " + e.getMessage());
        }

        return posts;
    }

}
