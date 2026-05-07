package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import services.ReminderService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainView {

    private BorderPane root;
    private StackPane contentArea;
    private ReminderService reminderService;
    private TodayView todayView;
    private AllTasksView allTasksView;
    private AddTaskView addTaskView;
    private RemindersView remindersView;
    private HBox activeNavItem = null;

    public MainView() {
        reminderService = new ReminderService();
        buildUI();
        reminderService.start();
        showToday();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color:#111827;");

        root.setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color:#111827;");
        root.setCenter(contentArea);

        todayView     = new TodayView();
        allTasksView  = new AllTasksView();
        addTaskView   = new AddTaskView(() -> { allTasksView.refresh(); todayView.refresh(); showToday(); });
        remindersView = new RemindersView();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(245);
        sidebar.setStyle(
            "-fx-background-color: #0d1117;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 1 0 0;"
        );

        // ── Header ────────────────────────────────────────
        VBox header = new VBox(6);
        header.setPadding(new Insets(26, 20, 20, 20));
        header.setStyle(
            "-fx-background-color: #0d1117;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size:34px; -fx-effect: dropshadow(gaussian,#00d4aa,16,0.4,0,0);");

        Label name = new Label("TaskFlow");
        name.setStyle(
            "-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#00d4aa;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,212,170,0.5),10,0,0,0);"
        );

        Label date = new Label("📅  " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d")));
        date.setStyle("-fx-font-size:11px; -fx-text-fill:#374151;");

        header.getChildren().addAll(icon, name, date);

        // ── Nav ───────────────────────────────────────────
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(16, 12, 8, 12));
        nav.setStyle("-fx-background-color:transparent;");

        Label navTitle = new Label("MENU");
        navTitle.setStyle("-fx-font-size:9.5px; -fx-font-weight:bold; -fx-text-fill:#374151; -fx-padding:8 0 6 8;");

        HBox todayBtn  = navItem("🌤", "Today",      "#00d4aa", () -> showToday());
        HBox allBtn    = navItem("📋", "All Tasks",  "#3b82f6", () -> showAllTasks());
        HBox addBtn    = navItem("➕", "Add Task",   "#a855f7", () -> showAddTask());
        HBox remBtn    = navItem("⏰", "Reminders",  "#f59e0b", () -> showReminders());

        nav.getChildren().addAll(navTitle, todayBtn, allBtn, addBtn, remBtn);

        // ── Stat cards ────────────────────────────────────
        VBox statsSection = new VBox(10);
        statsSection.setPadding(new Insets(16, 14, 24, 14));
        statsSection.setStyle(
            "-fx-background-color:#0d1117;" +
            "-fx-border-color:#1f2937;" +
            "-fx-border-width:1 0 0 0;"
        );

        Label statsTitle = new Label("OVERVIEW");
        statsTitle.setStyle("-fx-font-size:9.5px; -fx-font-weight:bold; -fx-text-fill:#374151; -fx-padding:0 0 4 2;");

        int today   = TaskStore.getInstance().getTodayTasks().size();
        int total   = TaskStore.getInstance().getAllTasks().size();
        int overdue = TaskStore.getInstance().getOverdueTasks().size();

        statsSection.getChildren().addAll(
            statsTitle,
            statCard("🌤", "Today",   String.valueOf(today),
                     "#00d4aa", "#ffffff", "#0d2a22", "#00d4aa"),
            statCard("📊", "Total",   String.valueOf(total),
                     "#3b82f6", "#ffffff", "#0c1a2e", "#3b82f6"),
            statCard("🔴", "Overdue", String.valueOf(overdue),
                     overdue > 0 ? "#ef4444" : "#00d4aa",
                     "#ffffff",
                     overdue > 0 ? "#2d1010" : "#0d2a22",
                     overdue > 0 ? "#ef4444" : "#00d4aa")
        );

        // Version tag
        Label version = new Label("TaskFlow v1.0  ·  SQLite");
        version.setStyle("-fx-font-size:9px; -fx-text-fill:#1f2937; -fx-padding:0 0 8 14;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, nav, spacer, statsSection, version);
        return sidebar;
    }

    private HBox navItem(String emoji, String label, String accentColor, Runnable action) {
        HBox item = new HBox(14);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 14, 10, 14));
        item.setStyle(
            "-fx-background-color:transparent;" +
            "-fx-background-radius:10;" +
            "-fx-cursor:hand;"
        );

        // Left accent bar (hidden by default)
        Rectangle accent = new Rectangle(3, 26);
        accent.setArcWidth(3); accent.setArcHeight(3);
        accent.setFill(Color.TRANSPARENT);

        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size:16px;");

        Label textLbl = new Label(label);
        textLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#9ca3af;");

        item.getChildren().addAll(accent, emojiLbl, textLbl);

        item.setOnMouseEntered(e -> {
            if (item != activeNavItem)
                item.setStyle("-fx-background-color:#1f2937; -fx-background-radius:10; -fx-cursor:hand;");
        });
        item.setOnMouseExited(e -> {
            if (item != activeNavItem)
                item.setStyle("-fx-background-color:transparent; -fx-background-radius:10; -fx-cursor:hand;");
        });

        item.setOnMouseClicked(e -> {
            // Deactivate previous
            if (activeNavItem != null) {
                activeNavItem.setStyle("-fx-background-color:transparent; -fx-background-radius:10; -fx-cursor:hand;");
                Rectangle prevBar = (Rectangle) activeNavItem.getChildren().get(0);
                prevBar.setFill(Color.TRANSPARENT);
                Label prevText = (Label) activeNavItem.getChildren().get(2);
                prevText.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#9ca3af;");
            }
            // Activate this
            item.setStyle(
                "-fx-background-color:#1a2535;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:" + accentColor + ";" +
                "-fx-border-radius:10;" +
                "-fx-border-width:0 0 0 3;" +
                "-fx-cursor:hand;"
            );
            accent.setFill(Color.web(accentColor));
            textLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + accentColor + ";");
            activeNavItem = item;
            action.run();
        });

        return item;
    }

    private HBox statCard(String emoji, String label, String value,
                          String accentColor, String valueColor, String bgColor, String borderColor) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle(
            "-fx-background-color:" + bgColor + ";" +
            "-fx-background-radius:14;" +
            "-fx-border-color:" + borderColor + "44;" +
            "-fx-border-radius:14;" +
            "-fx-border-width:1;" +
            "-fx-effect: dropshadow(gaussian," + accentColor + "33,10,0,0,3);"
        );

        // Left accent bar on card
        Rectangle bar = new Rectangle(3, 36);
        bar.setArcWidth(3); bar.setArcHeight(3);
        bar.setFill(Color.web(accentColor));

        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size:20px;");

        VBox info = new VBox(2);
        info.setStyle("-fx-background-color:transparent;");
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lbl = new Label(label.toUpperCase());
        lbl.setStyle("-fx-font-size:9.5px; -fx-font-weight:bold; -fx-text-fill:" + accentColor + "; -fx-opacity:0.8;");

        Label val = new Label(value);
        val.setStyle(
            "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + valueColor + ";" +
            "-fx-effect: dropshadow(gaussian," + accentColor + "aa,8,0,0,0);"
        );

        info.getChildren().addAll(lbl, val);
        card.getChildren().addAll(bar, emojiLbl, info);
        return card;
    }

    private void showToday()     { todayView.refresh();     setContent(todayView.getView()); }
    private void showAllTasks()  { allTasksView.refresh();  setContent(allTasksView.getView()); }
    private void showAddTask()   {                          setContent(addTaskView.getView()); }
    private void showReminders() { remindersView.refresh(); setContent(remindersView.getView()); }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    public BorderPane getRoot() { return root; }
}
