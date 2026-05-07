package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Paths;

public class DatabaseManager {

    // Saves taskflow.db in the user's home directory
    // e.g. C:\Users\YourName\taskflow.db  or  /home/yourname/taskflow.db
    private static final String DB_PATH = Paths.get(
            System.getProperty("user.home"), "taskflow.db"
    ).toString();

    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private static Connection connection;

    // ── Get or open connection ────────────────────────────────────────────
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            // Enable WAL mode for better performance
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA journal_mode=WAL;");
                st.execute("PRAGMA foreign_keys=ON;");
            }
        }
        return connection;
    }

    // ── Create tables if they don't exist ────────────────────────────────
    public static void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id            TEXT PRIMARY KEY,
                    title         TEXT NOT NULL,
                    description   TEXT,
                    due_date      TEXT,
                    reminder_time TEXT,
                    priority      TEXT NOT NULL,
                    status        TEXT NOT NULL,
                    category      TEXT
                );
                """;
        try (Statement st = getConnection().createStatement()) {
            st.execute(sql);
            System.out.println("✅ Database initialized at: " + DB_PATH);
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize database: " + e.getMessage());
        }
    }

    // ── Close connection on app shutdown ─────────────────────────────────
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing DB: " + e.getMessage());
        }
    }
}
