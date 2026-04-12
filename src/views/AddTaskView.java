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

    private VBox view;
    private Runnable onTaskAdded;

    // Form fields
    private TextField titleField;
    private TextArea descArea;
    private DatePicker datePicker;
    private TextField reminderField;
    private ComboBox<Task.Priority> priorityBox;  // ✅ FIXED
    private TextField categoryField;
    private Label feedbackLabel;

    public AddTaskView(Runnable onTaskAdded) {
        this.onTaskAdded = onTaskAdded;
        buildUI();
    }

    private void buildUI() {
        view = new VBox();
        view.setFillWidth(true);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 32, 30, 32));
        content.setMaxWidth(640);

        Label title = new Label("➕ Add New Task");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Fill in the details to create a new task");
        subtitle.getStyleClass().add("page-subtitle");
        content.getChildren().addAll(title, subtitle);

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("form-card");
        formCard.setPadding(new Insets(28, 28, 28, 28));

        formCard.getChildren().add(formRow("Task Title *",
                titleField = styledField("e.g. Complete project report")));

        descArea = new TextArea();
        descArea.setPromptText("Describe your task...");
        descArea.setPrefRowCount(3);
        descArea.getStyleClass().add("styled-area");
        descArea.setWrapText(true);
        formCard.getChildren().add(formRow("Description", descArea));

        HBox dateRow = new HBox(16);
        dateRow.setFillHeight(true);

        datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("styled-date");
        datePicker.setPrefWidth(200);

        reminderField = styledField("HH:mm  e.g. 09:30");
        reminderField.setPrefWidth(160);

        VBox dateBox = new VBox(6, new Label("Due Date *") {{ getStyleClass().add("form-label"); }}, datePicker);
        VBox reminderBox = new VBox(6, new Label("Reminder Time") {{ getStyleClass().add("form-label"); }}, reminderField);

        HBox.setHgrow(dateBox, javafx.scene.layout.Priority.ALWAYS);   // ✅ FIXED
        HBox.setHgrow(reminderBox, javafx.scene.layout.Priority.ALWAYS);

        dateRow.getChildren().addAll(dateBox, reminderBox);
        formCard.getChildren().add(dateRow);

        HBox prioRow = new HBox(16);

        priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Task.Priority.values());  // ✅ FIXED
        priorityBox.setValue(Task.Priority.MEDIUM);             // ✅ FIXED
        priorityBox.getStyleClass().add("combo-filter");
        priorityBox.setPrefWidth(180);

        categoryField = styledField("e.g. Work, Personal");
        categoryField.setPrefWidth(200);

        VBox prioBox = new VBox(6, new Label("Priority") {{ getStyleClass().add("form-label"); }}, priorityBox);
        VBox catBox  = new VBox(6, new Label("Category") {{ getStyleClass().add("form-label"); }}, categoryField);

        HBox.setHgrow(prioBox, javafx.scene.layout.Priority.ALWAYS);   // ✅ FIXED
        HBox.setHgrow(catBox, javafx.scene.layout.Priority.ALWAYS);

        prioRow.getChildren().addAll(prioBox, catBox);
        formCard.getChildren().add(prioRow);

        feedbackLabel = new Label();
        feedbackLabel.getStyleClass().add("feedback-label");
        formCard.getChildren().add(feedbackLabel);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = new Button("✅ Add Task");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> handleAdd());

        Button clearBtn = new Button("🗑 Clear");
        clearBtn.getStyleClass().add("btn-secondary");
        clearBtn.setOnAction(e -> clearForm());

        buttons.getChildren().addAll(addBtn, clearBtn);
        formCard.getChildren().add(buttons);

        content.getChildren().add(formCard);

        HBox centered = new HBox(content);
        centered.setAlignment(Pos.TOP_CENTER);
        centered.setPadding(new Insets(0));

        scroll.setContent(centered);
        view.getChildren().add(scroll);

        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);   // ✅ FIXED
    }

    private void handleAdd() {
        feedbackLabel.setText("");
        feedbackLabel.getStyleClass().removeAll("error", "success");

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Task title is required.");
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            showError("Due date is required.");
            return;
        }

        LocalTime reminder = null;
        String reminderText = reminderField.getText().trim();
        if (!reminderText.isEmpty()) {
            try {
                reminder = LocalTime.parse(reminderText);
            } catch (DateTimeParseException ex) {
                showError("Invalid reminder time. Use HH:mm format (e.g. 09:30).");
                return;
            }
        }

        String category = categoryField.getText().trim();
        if (category.isEmpty()) category = "General";

        Task task = new Task(title, descArea.getText().trim(),
                date, reminder, priorityBox.getValue(), category);

        TaskStore.getInstance().addTask(task);

        showSuccess("Task \"" + title + "\" added successfully!");
        clearForm();

        if (onTaskAdded != null) onTaskAdded.run();
    }

    private void clearForm() {
        titleField.clear();
        descArea.clear();
        datePicker.setValue(LocalDate.now());
        reminderField.clear();
        priorityBox.setValue(Task.Priority.MEDIUM);  // ✅ FIXED
        categoryField.clear();
        feedbackLabel.setText("");
    }

    private void showError(String msg) {
        feedbackLabel.setText("⚠ " + msg);
        feedbackLabel.getStyleClass().add("error");
    }

    private void showSuccess(String msg) {
        feedbackLabel.setText("✅ " + msg);
        feedbackLabel.getStyleClass().add("success");
    }

    private VBox formRow(String labelText, javafx.scene.Node input) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("form-label");
        return new VBox(6, lbl, input);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("styled-field");
        return tf;
    }

    public VBox getView() { return view; }
}