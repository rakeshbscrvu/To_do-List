package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Task;
import java.util.List;
import java.util.stream.Collectors;

public class RemindersView {

    private static final String BG      = "#111827";
    private static final String CARD_BG = "#1f2937";
    private static final String TEAL    = "#00d4aa";
    private static final String VIOLET  = "#a855f7";
    private static final String BORDER  = "#374151";

    private VBox view;
    public RemindersView() { view = new VBox(); view.setFillWidth(true); view.setStyle("-fx-background-color:" + BG + ";"); }

    public void refresh() {
        view.getChildren().clear();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");
        scroll.getStyleClass().add("transparent-scroll");

        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setFillWidth(true);
        content.setStyle("-fx-background-color:" + BG + ";");

        Label title = new Label("⏰   Reminders");
        title.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label("Tasks with scheduled reminders");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#6b7280;");
        content.getChildren().addAll(title, sub);

        List<Task> all = TaskStore.getInstance().getAllTasks().stream()
                .filter(t -> t.getReminderTime() != null && t.getStatus() != Task.Status.DONE)
                .sorted((a, b) -> a.getReminderTime().compareTo(b.getReminderTime()))
                .collect(Collectors.toList());

        List<Task> todayRem    = all.stream().filter(Task::isDueToday).collect(Collectors.toList());
        List<Task> upcomingRem = all.stream().filter(t -> !t.isDueToday() && !t.isOverdue()).collect(Collectors.toList());

        content.getChildren().add(sectionBadge("📅   TODAY'S REMINDERS", todayRem.size(), TEAL));
        if (todayRem.isEmpty())
            content.getChildren().add(emptyNote("No reminders set for today."));
        else
            for (Task t : todayRem) content.getChildren().add(reminderCard(t));

        content.getChildren().add(sectionBadge("🗓   UPCOMING REMINDERS", upcomingRem.size(), VIOLET));
        if (upcomingRem.isEmpty())
            content.getChildren().add(emptyNote("No upcoming reminders."));
        else
            for (Task t : upcomingRem) content.getChildren().add(reminderCard(t));

        scroll.setContent(content);
        scroll.skinProperty().addListener((obs, o, n) -> {
            if (n != null) scroll.lookup(".viewport").setStyle("-fx-background-color:" + BG + ";");
        });

        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private HBox sectionBadge(String text, int count, String color) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 4, 0));
        row.setStyle("-fx-background-color:transparent;");
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        Label cnt = new Label(String.valueOf(count));
        cnt.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:" + color + ";" +
                "-fx-background-color:" + color + "22; -fx-background-radius:20;" +
                "-fx-border-color:" + color + "55; -fx-border-radius:20; -fx-border-width:1; -fx-padding:2 10 2 10;");
        row.getChildren().addAll(lbl, cnt);
        return row;
    }

    private HBox reminderCard(Task task) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle(
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-background-radius:14;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-radius:14; -fx-border-width:1;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),10,0,0,3);"
        );

        VBox bubble = new VBox();
        bubble.setAlignment(Pos.CENTER);
        bubble.setMinWidth(74);
        bubble.setPadding(new Insets(10, 8, 10, 8));
        bubble.setStyle(
            "-fx-background-color:" + TEAL + ";" +
            "-fx-background-radius:12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,212,170,0.5),12,0,0,0);"
        );
        Label timeLbl = new Label(task.getReminderTime().toString());
        timeLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#0d1117;");
        bubble.getChildren().add(timeLbl);

        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label tl = new Label(task.getTitle());
        tl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#f3f4f6;");
        HBox meta = new HBox(14);
        meta.setStyle("-fx-background-color:transparent;");
        meta.getChildren().addAll(
            metaLbl("📅 " + task.getDueDate()),
            metaLbl("🏷 " + task.getCategory()),
            metaLbl("⚡ " + task.getPriority())
        );
        info.getChildren().addAll(tl, meta);
        info.setStyle("-fx-background-color:transparent;");

        Label badge = new Label(task.getPriority().name());
        badge.getStyleClass().addAll("priority-badge", "priority-" + task.getPriority().name().toLowerCase());

        card.getChildren().addAll(bubble, info, badge);
        return card;
    }

    private Label metaLbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:11.5px; -fx-text-fill:#6b7280;");
        return l;
    }

    private Label emptyNote(String text) {
        Label l = new Label("💤   " + text);
        l.setStyle("-fx-font-size:13px; -fx-text-fill:#374151; -fx-font-weight:bold; -fx-padding:8 0 4 0;");
        return l;
    }

    public VBox getView() { return view; }
}
