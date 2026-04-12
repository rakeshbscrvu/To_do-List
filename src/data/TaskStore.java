package data;

import models.Task;
import models.Task.Priority;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskStore {

    private static TaskStore instance;
    private List<Task> tasks = new ArrayList<>();

    private TaskStore() {
        loadSampleData();
    }

    public static TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }

    private void loadSampleData() {
        tasks.add(new Task("Morning Stand-up Meeting",
                "Daily team sync at 9 AM", LocalDate.now(),
                LocalTime.of(8, 45), Priority.HIGH, "Work"));

        tasks.add(new Task("Submit Project Report",
                "Quarterly performance report", LocalDate.now(),
                LocalTime.of(10, 0), Priority.HIGH, "Work"));

        tasks.add(new Task("Buy Groceries",
                "Milk, eggs, bread, fruits", LocalDate.now(),
                LocalTime.of(17, 0), Priority.MEDIUM, "Personal"));

        tasks.add(new Task("Exercise - 30 mins",
                "Jog or gym session", LocalDate.now().plusDays(1),
                LocalTime.of(7, 0), Priority.MEDIUM, "Health"));

        tasks.add(new Task("Read JavaFX Documentation",
                "Study scene graph and CSS styling", LocalDate.now().plusDays(2),
                LocalTime.of(20, 0), Priority.LOW, "Learning"));

        tasks.add(new Task("Pay Electricity Bill",
                "Online payment before due date", LocalDate.now().minusDays(1),
                LocalTime.of(9, 0), Priority.HIGH, "Finance"));
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTodayTasks() {
        return tasks.stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByCategory(String category) {
        return tasks.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasks(String keyword) {
        String lower = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lower)
                        || t.getDescription().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<String> getCategories() {
        return tasks.stream()
                .map(Task::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
