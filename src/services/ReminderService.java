package services;

import data.TaskStore;
import models.Task;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReminderService {

    private Timeline timeline;
    private Set<String> notifiedIds = new HashSet<>();

    public void start() {
        // Check immediately on start
        checkReminders();

        // Then check every 30 seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> checkReminders()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    private void checkReminders() {
        LocalTime now = LocalTime.now();
        // Check ALL tasks not just today — in case of timezone issues
        List<Task> tasks = TaskStore.getInstance().getAllTasks();

        for (Task task : tasks) {
            if (task.getReminderTime() == null)            continue;
            if (task.getStatus() == Task.Status.DONE)     continue;
            if (notifiedIds.contains(task.getId()))       continue;
            if (!task.isDueToday())                       continue;

            LocalTime reminder = task.getReminderTime();

            // Fire if current time is within a 3-minute window of reminder time
            boolean inWindow = !now.isBefore(reminder) &&
                                now.isBefore(reminder.plusMinutes(3));

            if (inWindow) {
                notifiedIds.add(task.getId());
                System.out.println("Firing reminder for: " + task.getTitle());
                Platform.runLater(() -> showReminderAlert(task));
            }
        }
    }

    private void showReminderAlert(Task task) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("TaskFlow Reminder");
        alert.setHeaderText("Reminder: " + task.getTitle());
        alert.setContentText(
            "Description: " + (task.getDescription().isEmpty() ? "No description" : task.getDescription()) + "\n" +
            "Category:    " + task.getCategory() + "\n" +
            "Priority:    " + task.getPriority() + "\n" +
            "Due:         " + task.getDueDate()
        );

        // Safely load CSS — don't crash if file missing
        try {
            var css = getClass().getResource("/styles/style.css");
            if (css != null) {
                alert.getDialogPane().getStylesheets().add(css.toExternalForm());
            }
        } catch (Exception e) {
            System.out.println("Could not load CSS for alert: " + e.getMessage());
        }

        alert.show();
    }
}
