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

public class EditTaskDialog {

    /**
     * Opens a pre-filled edit dialog for the given task.
     * On save, updates the task in DB and calls onSaved so the view refreshes.
     */
    public static void show(Task task, Runnable onSaved) {

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("✏ Edit Task");
        dialog.setHeaderText("Edit: " + task.getTitle());
        dialog.getDialogPane().setPrefWidth(500);

        // ── Apply CSS if available ────────────────────────────────────────
        var css = EditTaskDialog.class.getResource("/styles/style.css");
        if (css != null) dialog.getDialogPane().getStylesheets().add(css.toExternalForm());

        // ── Form fields ───────────────────────────────────────────────────
        TextField titleField = new TextField(task.getTitle());
        titleField.getStyleClass().add("styled-field");

        TextArea descArea = new TextArea(task.getDescription());
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("styled-area");

        DatePicker datePicker = new DatePicker(task.getDueDate());
        datePicker.getStyleClass().add("styled-date");

        TextField reminderField = new TextField(
                task.getReminderTime() != null ? task.getReminderTime().toString() : "");
        reminderField.setPromptText("HH:mm  e.g. 09:30");
        reminderField.getStyleClass().add("styled-field");

        ComboBox<Task.Priority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Task.Priority.values());
        priorityBox.setValue(task.getPriority());
        priorityBox.getStyleClass().add("combo-filter");

        ComboBox<Task.Status> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Task.Status.values());
        statusBox.setValue(task.getStatus());
        statusBox.getStyleClass().add("combo-filter");

        TextField categoryField = new TextField(task.getCategory());
        categoryField.getStyleClass().add("styled-field");

        Label feedbackLabel = new Label();
        feedbackLabel.getStyleClass().add("feedback-label");

        // ── Layout ────────────────────────────────────────────────────────
        VBox form = new VBox(14);
        form.setPadding(new Insets(20));

        form.getChildren().addAll(
                row("Task Title *",     titleField),
                row("Description",      descArea),
                row("Due Date *",       datePicker),
                row("Reminder Time",    reminderField),
                twoCol("Priority",      priorityBox, "Status", statusBox),
                row("Category",         categoryField),
                feedbackLabel
        );

        dialog.getDialogPane().setContent(form);

        // ── Buttons ───────────────────────────────────────────────────────
        ButtonType saveBtn   = new ButtonType("💾 Save",   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel",    ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        // ── Validate and save on OK ───────────────────────────────────────
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    feedbackLabel.setText("⚠ Title is required.");
                    return null; // keep dialog open
                }
                if (datePicker.getValue() == null) {
                    feedbackLabel.setText("⚠ Due date is required.");
                    return null;
                }
                LocalTime reminder = null;
                String remText = reminderField.getText().trim();
                if (!remText.isEmpty()) {
                    try {
                        reminder = LocalTime.parse(remText);
                    } catch (DateTimeParseException e) {
                        feedbackLabel.setText("⚠ Invalid time. Use HH:mm e.g. 09:30");
                        return null;
                    }
                }
                // Apply changes to task object
                task.setTitle(title);
                task.setDescription(descArea.getText().trim());
                task.setDueDate(datePicker.getValue());
                task.setReminderTime(reminder);
                task.setPriority(priorityBox.getValue());
                task.setStatus(statusBox.getValue());
                String cat = categoryField.getText().trim();
                task.setCategory(cat.isEmpty() ? "General" : cat);

                // Persist full update to DB
                TaskStore.getInstance().updateTask(task);
                return true;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(saved -> {
            if (saved && onSaved != null) onSaved.run();
        });
    }

    // ── Helper: single label + field row ─────────────────────────────────
    private static VBox row(String labelText, javafx.scene.Node input) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("form-label");
        VBox box = new VBox(5, lbl, input);
        if (input instanceof TextField tf) tf.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    // ── Helper: two side-by-side fields ──────────────────────────────────
    private static HBox twoCol(String lbl1, javafx.scene.Node n1,
                                String lbl2, javafx.scene.Node n2) {
        VBox left  = new VBox(5, new Label(lbl1) {{ getStyleClass().add("form-label"); }}, n1);
        VBox right = new VBox(5, new Label(lbl2) {{ getStyleClass().add("form-label"); }}, n2);
        HBox.setHgrow(left,  Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        HBox row = new HBox(16, left, right);
        row.setFillHeight(true);
        return row;
    }
}
