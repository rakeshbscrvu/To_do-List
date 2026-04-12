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
        // Check every 30 seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> checkReminders()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Also check immediately on start
        checkReminders();
    }

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    private void checkReminders() {
        LocalTime now = LocalTime.now();
        List<Task> tasks = TaskStore.getInstance().getTodayTasks();

        for (Task task : tasks) {
            if (task.getReminderTime() == null) continue;
            if (task.getStatus() == Task.Status.DONE) continue;
            if (notifiedIds.contains(task.getId())) continue;

            LocalTime reminder = task.getReminderTime();
            // Trigger if within 2-minute window
            if (!now.isBefore(reminder) && now.isBefore(reminder.plusMinutes(2))) {
                notifiedIds.add(task.getId());
                Platform.runLater(() -> showReminderAlert(task));
            }
        }
    }

    private void showReminderAlert(Task task) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("⏰ Task Reminder");
        alert.setHeaderText("Reminder: " + task.getTitle());
        alert.setContentText(
                "📋 " + task.getDescription() + "\n" +
                "🏷 Category: " + task.getCategory() + "\n" +
                "⚡ Priority: " + task.getPriority()
        );
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/style.css").toExternalForm()
        );
        alert.show();
    }
}
