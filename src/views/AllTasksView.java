package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Task;
import java.util.List;
import java.util.stream.Collectors;

public class AllTasksView {

    private static final String BG      = "#111827";
    private static final String CARD_BG = "#1f2937";
    private static final String TEAL    = "#00d4aa";
    private static final String BORDER  = "#374151";

    private VBox view;
    private VBox taskList;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> statusFilter;

    public AllTasksView() { buildUI(); }

    private void buildUI() {
        view = new VBox();
        view.setFillWidth(true);
        view.setStyle("-fx-background-color:" + BG + ";");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");
        scroll.getStyleClass().add("transparent-scroll");

        VBox content = new VBox(18);
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setFillWidth(true);
        content.setStyle("-fx-background-color:" + BG + ";");

        // Header
        Label title = new Label("📋   All Tasks");
        title.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label("Search, filter and manage everything");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#6b7280;");
        content.getChildren().addAll(title, sub);

        // Filter bar
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 18, 14, 18));
        bar.setStyle(
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-background-radius:14;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-radius:14; -fx-border-width:1;"
        );

        searchField = new TextField();
        searchField.setPromptText("🔍   Search tasks...");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        categoryFilter = new ComboBox<>();
        categoryFilter.getStyleClass().add("combo-filter");
        categoryFilter.setPrefWidth(150);
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(TaskStore.getInstance().getCategories());
        categoryFilter.setValue("All Categories");

        statusFilter = new ComboBox<>();
        statusFilter.getStyleClass().add("combo-filter");
        statusFilter.setPrefWidth(135);
        statusFilter.getItems().addAll("All Status", "PENDING", "IN_PROGRESS", "DONE");
        statusFilter.setValue("All Status");

        Button filterBtn = new Button("⚡   Filter");
        filterBtn.getStyleClass().add("btn-primary");
        filterBtn.setOnAction(e -> applyFilters());
        searchField.setOnAction(e -> applyFilters());

        bar.getChildren().addAll(searchField, categoryFilter, statusFilter, filterBtn);
        content.getChildren().add(bar);

        taskList = new VBox(10);
        taskList.setFillWidth(true);
        taskList.setStyle("-fx-background-color:transparent;");
        content.getChildren().add(taskList);

        scroll.setContent(content);
        scroll.skinProperty().addListener((obs, o, n) -> {
            if (n != null) scroll.lookup(".viewport").setStyle("-fx-background-color:" + BG + ";");
        });

        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private void applyFilters() {
        String kw  = searchField.getText().trim();
        String cat = categoryFilter.getValue();
        String st  = statusFilter.getValue();
        List<Task> tasks = kw.isEmpty()
                ? TaskStore.getInstance().getAllTasks()
                : TaskStore.getInstance().searchTasks(kw);
        if (cat != null && !cat.equals("All Categories"))
            tasks = tasks.stream().filter(t -> t.getCategory().equalsIgnoreCase(cat)).collect(Collectors.toList());
        if (st != null && !st.equals("All Status"))
            tasks = tasks.stream().filter(t -> t.getStatus() == Task.Status.valueOf(st)).collect(Collectors.toList());
        renderTasks(tasks);
    }

    public void refresh() {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(TaskStore.getInstance().getCategories());
        categoryFilter.setValue("All Categories");
        statusFilter.setValue("All Status");
        searchField.clear();
        renderTasks(TaskStore.getInstance().getAllTasks());
    }

    private void renderTasks(List<Task> tasks) {
        taskList.getChildren().clear();
        if (tasks.isEmpty()) {
            VBox empty = new VBox(8);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(50, 0, 20, 0));
            empty.setStyle("-fx-background-color:transparent;");
            Label e = new Label("🔍"); e.setStyle("-fx-font-size:42px;");
            Label t = new Label("No tasks found");
            t.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#374151;");
            empty.getChildren().addAll(e, t);
            taskList.getChildren().add(empty);
        } else {
            for (Task t : tasks) taskList.getChildren().add(TaskCard.build(t, false));
        }
    }

    public VBox getView() { return view; }
}
