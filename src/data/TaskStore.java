package data;

import models.Task;
import models.Task.Priority;
import models.Task.Status;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TaskStore {

    private static TaskStore instance;

    private TaskStore() {
    }

    public static TaskStore getInstance() {
        if (instance == null)
            instance = new TaskStore();
        return instance;
    }

    // ── Map a ResultSet row → Task object ────────────────────────────────
    private Task mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String dueDateStr = rs.getString("due_date");
        String remTimeStr = rs.getString("reminder_time");
        String priorityStr = rs.getString("priority");
        String statusStr = rs.getString("status");
        String category = rs.getString("category");

        LocalDate dueDate = (dueDateStr != null && !dueDateStr.isEmpty()) ? LocalDate.parse(dueDateStr) : null;
        LocalTime reminderTime = (remTimeStr != null && !remTimeStr.isEmpty()) ? LocalTime.parse(remTimeStr) : null;

        Task task = new Task(title, description, dueDate, reminderTime,
                Priority.valueOf(priorityStr), category);
        task.setId(id);
        task.setStatus(Status.valueOf(statusStr));
        return task;
    }

    // ── READ: all tasks ───────────────────────────────────────────────────
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY due_date ASC";
        try (Statement st = DatabaseManager.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("getAllTasks error: " + e.getMessage());
        }
        return list;
    }

    // ── READ: today's tasks ───────────────────────────────────────────────
    public List<Task> getTodayTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE due_date = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, LocalDate.now().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("getTodayTasks error: " + e.getMessage());
        }
        return list;
    }

    // ── READ: overdue tasks ───────────────────────────────────────────────
    public List<Task> getOverdueTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE due_date < ? AND status != 'DONE'";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, LocalDate.now().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("getOverdueTasks error: " + e.getMessage());
        }
        return list;
    }

    // ── READ: by category ────────────────────────────────────────────────
    public List<Task> getTasksByCategory(String category) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE LOWER(category) = LOWER(?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("getTasksByCategory error: " + e.getMessage());
        }
        return list;
    }

    // ── READ: search ─────────────────────────────────────────────────────
    public List<Task> searchTasks(String keyword) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE LOWER(title) LIKE ? OR LOWER(description) LIKE ?";
        String p = "%" + keyword.toLowerCase() + "%";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("searchTasks error: " + e.getMessage());
        }
        return list;
    }

    // ── READ: distinct categories ─────────────────────────────────────────
    public List<String> getCategories() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM tasks WHERE category IS NOT NULL ORDER BY category ASC";
        try (Statement st = DatabaseManager.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(rs.getString("category"));
        } catch (SQLException e) {
            System.err.println("getCategories error: " + e.getMessage());
        }
        return list;
    }

    // ── CREATE ────────────────────────────────────────────────────────────
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (id,title,description,due_date,reminder_time,priority,status,category) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, task.getId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getDueDate() != null ? task.getDueDate().toString() : null);
            ps.setString(5, task.getReminderTime() != null ? task.getReminderTime().toString() : null);
            ps.setString(6, task.getPriority().name());
            ps.setString(7, task.getStatus().name());
            ps.setString(8, task.getCategory());
            ps.executeUpdate();
            System.out.println("Saved: " + task.getTitle());
        } catch (SQLException e) {
            System.err.println("addTask error: " + e.getMessage());
        }
    }

    // ── UPDATE status only (called from TaskCard) ─────────────────────────
    public void updateTaskStatus(Task task) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, task.getStatus().name());
            ps.setString(2, task.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updateTaskStatus error: " + e.getMessage());
        }
    }

    // ── UPDATE full task ──────────────────────────────────────────────────
    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET title=?,description=?,due_date=?,reminder_time=?,priority=?,status=?,category=? WHERE id=?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getDueDate() != null ? task.getDueDate().toString() : null);
            ps.setString(4, task.getReminderTime() != null ? task.getReminderTime().toString() : null);
            ps.setString(5, task.getPriority().name());
            ps.setString(6, task.getStatus().name());
            ps.setString(7, task.getCategory());
            ps.setString(8, task.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updateTask error: " + e.getMessage());
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    public void removeTask(Task task) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, task.getId());
            ps.executeUpdate();
            System.out.println("Deleted: " + task.getTitle());
        } catch (SQLException e) {
            System.err.println("removeTask error: " + e.getMessage());
        }
    }
}