package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Task;

import java.util.List;

public class AllTasksView {

    private VBox view;
    private VBox taskList;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> statusFilter;

    public AllTasksView() {
        buildUI();
    }

    private void buildUI() {
        view = new VBox();
        view.setFillWidth(true);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");

        VBox content = new VBox(16);
        content.setPadding(new Insets(30, 32, 30, 32));
        content.setFillWidth(true);

        // Title
        Label title = new Label("📋 All Tasks");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Manage and track all your tasks");
        subtitle.getStyleClass().add("page-subtitle");
        content.getChildren().addAll(title, subtitle);

        // Search + Filter bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.getStyleClass().add("filter-bar");
        filterBar.setPadding(new Insets(14, 16, 14, 16));

        searchField = new TextField();
        searchField.setPromptText("🔍 Search tasks...");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Category");
        categoryFilter.getStyleClass().add("combo-filter");
        categoryFilter.getItems().add("All");
        categoryFilter.getItems().addAll(TaskStore.getInstance().getCategories());
        categoryFilter.setValue("All");

        statusFilter = new ComboBox<>();
        statusFilter.setPromptText("Status");
        statusFilter.getStyleClass().add("combo-filter");
        statusFilter.getItems().addAll("All", "PENDING", "IN_PROGRESS", "DONE");
        statusFilter.setValue("All");

        Button applyBtn = new Button("Filter");
        applyBtn.getStyleClass().add("btn-primary");
        applyBtn.setOnAction(e -> applyFilters());

        searchField.setOnAction(e -> applyFilters());

        filterBar.getChildren().addAll(searchField, categoryFilter, statusFilter, applyBtn);
        content.getChildren().add(filterBar);

        // Task list
        taskList = new VBox(10);
        taskList.setFillWidth(true);
        content.getChildren().add(taskList);

        scroll.setContent(content);
        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private void applyFilters() {
        String keyword  = searchField.getText().trim();
        String category = categoryFilter.getValue();
        String status   = statusFilter.getValue();

        List<Task> tasks = TaskStore.getInstance().getAllTasks();

        if (!keyword.isEmpty()) {
            tasks = TaskStore.getInstance().searchTasks(keyword);
        }
        if (category != null && !category.equals("All")) {
            tasks = tasks.stream()
                    .filter(t -> t.getCategory().equalsIgnoreCase(category))
                    .collect(java.util.stream.Collectors.toList());
        }
        if (status != null && !status.equals("All")) {
            Task.Status s = Task.Status.valueOf(status);
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == s)
                    .collect(java.util.stream.Collectors.toList());
        }

        renderTasks(tasks);
    }

    public void refresh() {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All");
        categoryFilter.getItems().addAll(TaskStore.getInstance().getCategories());
        categoryFilter.setValue("All");
        statusFilter.setValue("All");
        searchField.clear();
        renderTasks(TaskStore.getInstance().getAllTasks());
    }

    private void renderTasks(List<Task> tasks) {
        taskList.getChildren().clear();
        if (tasks.isEmpty()) {
            Label empty = new Label("No tasks found.");
            empty.getStyleClass().add("empty-message");
            taskList.getChildren().add(empty);
        } else {
            for (Task t : tasks) taskList.getChildren().add(TaskCard.build(t, false));
        }
    }

    public VBox getView() { return view; }
}
