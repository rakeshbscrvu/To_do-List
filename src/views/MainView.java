package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import services.ReminderService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainView {

    private BorderPane root;
    private StackPane contentArea;
    private ReminderService reminderService;

    // Sub-views
    private TodayView todayView;
    private AllTasksView allTasksView;
    private AddTaskView addTaskView;
    private RemindersView remindersView;

    public MainView() {
        reminderService = new ReminderService();
        buildUI();
        reminderService.start();
        showToday(); // Default view
    }

    private void buildUI() {
        root = new BorderPane();

        // Background image via CSS background
        root.getStyleClass().add("root-bg");

        // Sidebar
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        // Content area (center)
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        // Init sub-views
        todayView     = new TodayView();
        allTasksView  = new AllTasksView();
        addTaskView   = new AddTaskView(() -> {
            allTasksView.refresh();
            todayView.refresh();
            showToday();
        });
        remindersView = new RemindersView();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(230);

        // App logo / header
        VBox header = new VBox(6);
        header.getStyleClass().add("sidebar-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(28, 20, 20, 20));

        Label appIcon = new Label("✅");
        appIcon.getStyleClass().add("app-icon");

        Label appName = new Label("TaskFlow");
        appName.getStyleClass().add("app-name");

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"));
        Label dateLabel = new Label(today);
        dateLabel.getStyleClass().add("sidebar-date");

        header.getChildren().addAll(appIcon, appName, dateLabel);

        // Nav items
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(16, 12, 16, 12));

        Label navTitle = new Label("NAVIGATION");
        navTitle.getStyleClass().add("nav-section-title");

        HBox todayBtn      = navItem("🌤", "Today",           () -> showToday());
        HBox allBtn        = navItem("📋", "All Tasks",       () -> showAllTasks());
        HBox addBtn        = navItem("➕", "Add Task",        () -> showAddTask());
        HBox remindersBtn  = navItem("⏰", "Reminders",       () -> showReminders());

        nav.getChildren().addAll(navTitle, todayBtn, allBtn, addBtn, remindersBtn);

        // Stats at bottom
        VBox stats = buildSidebarStats();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, nav, spacer, stats);
        return sidebar;
    }

    private HBox navItem(String icon, String label, Runnable action) {
        HBox item = new HBox(12);
        item.getStyleClass().add("nav-item");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 14, 10, 14));

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("nav-icon");

        Label textLabel = new Label(label);
        textLabel.getStyleClass().add("nav-label");

        item.getChildren().addAll(iconLabel, textLabel);
        item.setOnMouseClicked(e -> {
            // Remove active from all siblings
            if (item.getParent() != null) {
                item.getParent().getChildrenUnmodifiable().forEach(
                        c -> c.getStyleClass().remove("nav-item-active")
                );
            }
            item.getStyleClass().add("nav-item-active");
            action.run();
        });

        return item;
    }

    private VBox buildSidebarStats() {
        VBox stats = new VBox(8);
        stats.getStyleClass().add("sidebar-stats");
        stats.setPadding(new Insets(16, 16, 24, 16));

        Label title = new Label("OVERVIEW");
        title.getStyleClass().add("nav-section-title");

        int todayCount   = data.TaskStore.getInstance().getTodayTasks().size();
        int totalCount   = data.TaskStore.getInstance().getAllTasks().size();
        int overdueCount = data.TaskStore.getInstance().getOverdueTasks().size();

        Label todayLbl   = new Label("📅 Today: " + todayCount + " tasks");
        Label totalLbl   = new Label("📊 Total: " + totalCount + " tasks");
        Label overdueLbl = new Label("🔴 Overdue: " + overdueCount);

        todayLbl.getStyleClass().add("stat-label");
        totalLbl.getStyleClass().add("stat-label");
        overdueLbl.getStyleClass().add("stat-label");
        if (overdueCount > 0) overdueLbl.getStyleClass().add("stat-overdue");

        stats.getChildren().addAll(title, todayLbl, totalLbl, overdueLbl);
        return stats;
    }

    // --- Navigation ---
    private void showToday()     { todayView.refresh();     setContent(todayView.getView()); }
    private void showAllTasks()  { allTasksView.refresh();  setContent(allTasksView.getView()); }
    private void showAddTask()   {                          setContent(addTaskView.getView()); }
    private void showReminders() { remindersView.refresh(); setContent(remindersView.getView()); }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    public BorderPane getRoot() { return root; }
}
