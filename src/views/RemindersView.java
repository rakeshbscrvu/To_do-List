package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Task;

import java.util.List;
import java.util.stream.Collectors;

public class RemindersView {

    private VBox view;

    public RemindersView() {
        view = new VBox();
        view.setFillWidth(true);
    }

    public void refresh() {
        view.getChildren().clear();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");

        VBox content = new VBox(16);
        content.setPadding(new Insets(30, 32, 30, 32));
        content.setFillWidth(true);

        Label title = new Label("⏰ Reminders");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Tasks with scheduled reminders");
        subtitle.getStyleClass().add("page-subtitle");
        content.getChildren().addAll(title, subtitle);

        // Tasks with reminders, sorted by reminder time
        List<Task> withReminders = TaskStore.getInstance().getAllTasks().stream()
                .filter(t -> t.getReminderTime() != null && t.getStatus() != Task.Status.DONE)
                .sorted((a, b) -> a.getReminderTime().compareTo(b.getReminderTime()))
                .collect(Collectors.toList());

        // Today's reminders
        List<Task> todayReminders = withReminders.stream()
                .filter(Task::isDueToday)
                .collect(Collectors.toList());

        Label todayLbl = new Label("📅 Today's Reminders — " + todayReminders.size());
        todayLbl.getStyleClass().add("section-label");
        content.getChildren().add(todayLbl);

        if (todayReminders.isEmpty()) {
            content.getChildren().add(emptyNote("No reminders set for today."));
        } else {
            for (Task t : todayReminders) content.getChildren().add(buildReminderCard(t));
        }

        // Upcoming reminders
        List<Task> upcoming = withReminders.stream()
                .filter(t -> !t.isDueToday() && !t.isOverdue())
                .collect(Collectors.toList());

        Label upcomingLbl = new Label("🗓 Upcoming Reminders — " + upcoming.size());
        upcomingLbl.getStyleClass().add("section-label");
        content.getChildren().add(upcomingLbl);

        if (upcoming.isEmpty()) {
            content.getChildren().add(emptyNote("No upcoming reminders."));
        } else {
            for (Task t : upcoming) content.getChildren().add(buildReminderCard(t));
        }

        scroll.setContent(content);
        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private HBox buildReminderCard(Task task) {
        HBox card = new HBox(16);
        card.getStyleClass().add("reminder-card");
        card.setPadding(new Insets(14, 18, 14, 18));

        // Time bubble
        VBox timeBubble = new VBox();
        timeBubble.getStyleClass().add("time-bubble");
        timeBubble.setPrefWidth(70);
        timeBubble.setAlignment(javafx.geometry.Pos.CENTER);
        Label timeLbl = new Label(task.getReminderTime().toString());
        timeLbl.getStyleClass().add("time-text");
        timeBubble.getChildren().add(timeLbl);

        // Task info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLbl = new Label(task.getTitle());
        titleLbl.getStyleClass().add("card-title");

        Label meta = new Label("📅 " + task.getDueDate() + "  •  🏷 " + task.getCategory()
                + "  •  ⚡ " + task.getPriority());
        meta.getStyleClass().add("card-meta");

        info.getChildren().addAll(titleLbl, meta);

        // Priority badge
        Label badge = new Label(task.getPriority().name());
        badge.getStyleClass().addAll("priority-badge", "priority-" + task.getPriority().name().toLowerCase());

        card.getChildren().addAll(timeBubble, info, badge);
        return card;
    }

    private Label emptyNote(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("empty-message");
        return lbl;
    }

    public VBox getView() { return view; }
}
