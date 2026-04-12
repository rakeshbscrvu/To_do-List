package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Task;

import java.util.List;

public class TodayView {

    private VBox view;

    public TodayView() {
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

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("🌤 Today's Tasks");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Focus on what matters today");
        subtitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, subtitle);
        header.getChildren().add(titleBox);
        content.getChildren().add(header);

        // Overdue section
        List<Task> overdue = TaskStore.getInstance().getOverdueTasks();
        if (!overdue.isEmpty()) {
            Label overdueTitle = new Label("⚠ Overdue");
            overdueTitle.getStyleClass().add("section-label");
            overdueTitle.getStyleClass().add("overdue-label");
            content.getChildren().add(overdueTitle);
            for (Task t : overdue) content.getChildren().add(TaskCard.build(t, true));
        }

        // Today tasks
        List<Task> todayTasks = TaskStore.getInstance().getTodayTasks();
        Label todayTitle = new Label("📅 Due Today — " + todayTasks.size() + " task(s)");
        todayTitle.getStyleClass().add("section-label");
        content.getChildren().add(todayTitle);

        if (todayTasks.isEmpty()) {
            Label empty = new Label("🎉 All clear! No tasks due today.");
            empty.getStyleClass().add("empty-message");
            content.getChildren().add(empty);
        } else {
            for (Task t : todayTasks) {
                content.getChildren().add(TaskCard.build(t, false));
            }
        }

        scroll.setContent(content);
        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    public VBox getView() { return view; }
}
