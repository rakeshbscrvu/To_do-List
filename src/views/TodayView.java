package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import models.Task;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TodayView {

    private static final String BG       = "#111827";
    private static final String CARD_BG  = "#1f2937";
    private static final String TEAL     = "#00d4aa";
    private static final String RED      = "#ef4444";
    private static final String BORDER   = "#374151";

    private VBox view;

    public TodayView() {
        view = new VBox();
        view.setFillWidth(true);
        view.setStyle("-fx-background-color:" + BG + ";");
    }

    public void refresh() {
        view.getChildren().clear();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");
        scroll.getStyleClass().add("transparent-scroll");

        VBox content = new VBox(18);
        content.setPadding(new Insets(28, 32, 32, 32));
        content.setFillWidth(true);
        content.setStyle("-fx-background-color:" + BG + ";");

        // ── Hero banner ───────────────────────────────────
        VBox banner = new VBox(8);
        banner.setPadding(new Insets(26, 30, 26, 30));
        banner.setStyle(
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-background-radius: 18;" +
            "-fx-border-color:" + TEAL + "44;" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,212,170,0.2), 22, 0, 0, 5);"
        );

        Label greeting = new Label("Good day!  👋");
        greeting.setStyle("-fx-font-size:12px; -fx-text-fill:" + TEAL + "; -fx-font-weight:bold;");

        Label dayLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE")));
        dayLabel.setStyle("-fx-font-size:42px; -fx-font-weight:bold; -fx-text-fill:white;" +
                "-fx-effect: dropshadow(gaussian," + TEAL + "55, 10, 0, 0, 0);");

        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#6b7280;");

        int todayCnt = TaskStore.getInstance().getTodayTasks().size();
        int ovCnt    = TaskStore.getInstance().getOverdueTasks().size();

        HBox chips = new HBox(10);
        chips.setPadding(new Insets(6, 0, 0, 0));
        chips.getChildren().addAll(
            chip("📅  " + todayCnt + " due today", TEAL,  "#0d2520"),
            chip("⚠  "  + ovCnt    + " overdue",   ovCnt > 0 ? RED : TEAL, ovCnt > 0 ? "#2d1515" : "#0d2520")
        );

        banner.getChildren().addAll(greeting, dayLabel, dateLabel, chips);
        content.getChildren().add(banner);

        // ── Overdue ───────────────────────────────────────
        List<Task> overdue = TaskStore.getInstance().getOverdueTasks();
        if (!overdue.isEmpty()) {
            content.getChildren().add(sectionLabel("⚠   OVERDUE", RED));
            for (Task t : overdue) content.getChildren().add(TaskCard.build(t, true));
        }

        // ── Today ─────────────────────────────────────────
        List<Task> today = TaskStore.getInstance().getTodayTasks();
        content.getChildren().add(sectionLabel("🌤   TODAY'S TASKS  ·  " + today.size(), TEAL));

        if (today.isEmpty()) {
            content.getChildren().add(emptyState());
        } else {
            for (Task t : today) content.getChildren().add(TaskCard.build(t, false));
        }

        scroll.setContent(content);
        // Force viewport dark
        scroll.skinProperty().addListener((obs, o, n) -> {
            if (n != null) {
                scroll.lookup(".viewport").setStyle("-fx-background-color:" + BG + ";");
            }
        });

        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private Label sectionLabel(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + color +
                "; -fx-padding: 10 0 2 0;");
        return l;
    }

    private HBox chip(String text, String color, String bg) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + color +
                "; -fx-padding: 4 16 4 16; -fx-background-radius:20; -fx-background-color:" + bg +
                "; -fx-border-color:" + color + "66; -fx-border-radius:20; -fx-border-width:1;");
        return new HBox(l);
    }

    private VBox emptyState() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50, 0, 20, 0));
        box.setStyle("-fx-background-color:transparent;");
        Label e = new Label("🎉"); e.setStyle("-fx-font-size:48px;");
        Label t = new Label("All clear!");
        t.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#00d4aa;");
        Label s = new Label("No tasks due today — enjoy your day!");
        s.setStyle("-fx-font-size:13px; -fx-text-fill:#374151;");
        box.getChildren().addAll(e, t, s);
        return box;
    }

    public VBox getView() { return view; }
}
