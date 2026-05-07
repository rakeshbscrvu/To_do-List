package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Task;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AddTaskView {

    private static final String BG      = "#111827";
    private static final String CARD_BG = "#1f2937";
    private static final String TEAL    = "#00d4aa";
    private static final String BORDER  = "#374151";

    private VBox view;
    private Runnable onTaskAdded;
    private TextField titleField, reminderField, categoryField;
    private TextArea descArea;
    private DatePicker datePicker;
    private ComboBox<Task.Priority> priorityBox;
    private Label feedbackLabel;

    public AddTaskView(Runnable onTaskAdded) {
        this.onTaskAdded = onTaskAdded;
        buildUI();
    }

    private void buildUI() {
        view = new VBox();
        view.setFillWidth(true);
        view.setStyle("-fx-background-color:" + BG + ";");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");
        scroll.getStyleClass().add("transparent-scroll");

        VBox outer = new VBox(0);
        outer.setPadding(new Insets(28, 32, 32, 32));
        outer.setFillWidth(true);
        outer.setStyle("-fx-background-color:" + BG + ";");

        // Header
        Label title = new Label("➕   Add New Task");
        title.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label sub = new Label("Fill in the details below to create a new task");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:#6b7280;");
        outer.getChildren().addAll(title, sub, gap(16));

        // Form card
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(620);
        card.setStyle(
            "-fx-background-color:" + CARD_BG + ";" +
            "-fx-background-radius:18;" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-radius:18; -fx-border-width:1;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.5),18,0,0,5);"
        );

        titleField = styledField("e.g. Complete project report");
        card.getChildren().add(row("📌   Task Title  *", titleField));

        descArea = new TextArea();
        descArea.setPromptText("Add more details about this task...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("styled-area");
        card.getChildren().add(row("📝   Description", descArea));

        // Date + Reminder row
        HBox dateRow = new HBox(16);
        datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("styled-date");
        datePicker.setPrefWidth(200);
        reminderField = styledField("HH:mm  e.g. 09:30");
        VBox dBox = colBox("📅   Due Date  *", datePicker);
        VBox rBox = colBox("⏰   Reminder Time", reminderField);
        HBox.setHgrow(dBox, Priority.ALWAYS);
        HBox.setHgrow(rBox, Priority.ALWAYS);
        dateRow.getChildren().addAll(dBox, rBox);
        card.getChildren().add(dateRow);

        // Priority + Category
        HBox prioRow = new HBox(16);
        priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Task.Priority.values());
        priorityBox.setValue(Task.Priority.MEDIUM);
        priorityBox.getStyleClass().add("combo-filter");
        priorityBox.setPrefWidth(190);
        categoryField = styledField("e.g. Work, Personal");
        VBox pBox = colBox("⚡   Priority", priorityBox);
        VBox cBox = colBox("🏷   Category", categoryField);
        HBox.setHgrow(pBox, Priority.ALWAYS);
        HBox.setHgrow(cBox, Priority.ALWAYS);
        prioRow.getChildren().addAll(pBox, cBox);
        card.getChildren().add(prioRow);

        feedbackLabel = new Label();
        feedbackLabel.getStyleClass().add("feedback-label");
        card.getChildren().add(feedbackLabel);

        HBox btns = new HBox(12);
        btns.setAlignment(Pos.CENTER_LEFT);
        Button saveBtn  = new Button("✅   Save Task");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> handleAdd());
        Button clearBtn = new Button("🗑   Clear");
        clearBtn.getStyleClass().add("btn-secondary");
        clearBtn.setOnAction(e -> clearForm());
        btns.getChildren().addAll(saveBtn, clearBtn);
        card.getChildren().add(btns);

        HBox centred = new HBox(card);
        centred.setAlignment(Pos.TOP_CENTER);
        centred.setStyle("-fx-background-color:transparent;");
        outer.getChildren().add(centred);

        scroll.setContent(outer);
        scroll.skinProperty().addListener((obs, o, n) -> {
            if (n != null) scroll.lookup(".viewport").setStyle("-fx-background-color:" + BG + ";");
        });

        view.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    private void handleAdd() {
        feedbackLabel.setText("");
        feedbackLabel.getStyleClass().removeAll("error", "success");
        String t = titleField.getText().trim();
        if (t.isEmpty()) { showError("Task title is required."); return; }
        if (datePicker.getValue() == null) { showError("Due date is required."); return; }
        LocalTime reminder = null;
        String rt = reminderField.getText().trim();
        if (!rt.isEmpty()) {
            try { reminder = LocalTime.parse(rt); }
            catch (DateTimeParseException ex) { showError("Invalid time. Use HH:mm e.g. 09:30"); return; }
        }
        String cat = categoryField.getText().trim();
        if (cat.isEmpty()) cat = "General";
        TaskStore.getInstance().addTask(
            new Task(t, descArea.getText().trim(), datePicker.getValue(), reminder, priorityBox.getValue(), cat));
        showSuccess("Task \"" + t + "\" saved!");
        clearForm();
        if (onTaskAdded != null) onTaskAdded.run();
    }

    private void clearForm() {
        titleField.clear(); descArea.clear();
        datePicker.setValue(LocalDate.now()); reminderField.clear();
        priorityBox.setValue(Task.Priority.MEDIUM); categoryField.clear();
        feedbackLabel.setText("");
    }

    private void showError(String m)   { feedbackLabel.setText("⚠  " + m); feedbackLabel.getStyleClass().add("error"); }
    private void showSuccess(String m) { feedbackLabel.setText("✅  " + m); feedbackLabel.getStyleClass().add("success"); }

    private VBox row(String lbl, javafx.scene.Node input) {
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#9ca3af;");
        return new VBox(6, l, input);
    }

    private VBox colBox(String lbl, javafx.scene.Node input) {
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#9ca3af;");
        VBox b = new VBox(6, l, input);
        return b;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("styled-field");
        return tf;
    }

    private Region gap(double h) { Region r = new Region(); r.setPrefHeight(h); return r; }

    public VBox getView() { return view; }
}
