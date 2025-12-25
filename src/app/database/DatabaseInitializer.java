package app.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String[] queries = {
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "phone TEXT," +
                        "email TEXT UNIQUE," +
                        "password TEXT," +
                        "role TEXT," +
                        "specialization TEXT," +
                        "experience INTEGER," +
                        "rating REAL," +
                        "location TEXT," +
                        "shopLocation TEXT," +
                        "delivery INTEGER" +
                        ");",

                "CREATE TABLE IF NOT EXISTS materials (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "price REAL," +
                        "quantity INTEGER," +
                        "supplierID INTEGER," +
                        "FOREIGN KEY(supplierID) REFERENCES users(id)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS orders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "customerID INTEGER," +
                        "materialID INTEGER," +
                        "quantity INTEGER," +
                        "total_price REAL," +
                        "status TEXT DEFAULT 'PENDING'," +
                        "FOREIGN KEY(customerID) REFERENCES users(id)," +
                        "FOREIGN KEY(materialID) REFERENCES materials(id)" +
                        ");",

                """
                CREATE TABLE IF NOT EXISTS posts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                         customerId INTEGER NOT NULL,
                         content TEXT NOT NULL,
                         location TEXT NOT NULL,
                         createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (customerId) REFERENCES users(id)
                );
                """,

                "CREATE TABLE IF NOT EXISTS worker_requests (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "customerId INTEGER NOT NULL," +
                        "workerId INTEGER NOT NULL," +
                        "message TEXT," +
                        "status TEXT DEFAULT 'PENDING'," +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY(customerId) REFERENCES users(id)," +
                        "FOREIGN KEY(workerId) REFERENCES users(id)" +
                        ");"
        };

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            for (String q : queries) stmt.execute(q);
            System.out.println("Database initialized (SQLite) ✔");

        } catch (Exception e) {
            System.out.println("Failed to initialize DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
