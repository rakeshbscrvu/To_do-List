package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Task;
import models.Task.Status;

public class TaskCard {

    public static HBox build(Task task, boolean isOverdue) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.getStyleClass().add("task-card");
        if (isOverdue) card.getStyleClass().add("task-card-overdue");
        if (task.getStatus() == Status.DONE) card.getStyleClass().add("task-card-done");

        // Status checkbox style toggle
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(task.getStatus() == Status.DONE);
        checkBox.getStyleClass().add("task-checkbox");
        checkBox.setOnAction(e -> {
            task.setStatus(checkBox.isSelected() ? Status.DONE : Status.PENDING);
            if (checkBox.isSelected()) {
                card.getStyleClass().add("task-card-done");
            } else {
                card.getStyleClass().remove("task-card-done");
            }
        });

        // Info section
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLbl = new Label(task.getTitle());
        titleLbl.getStyleClass().add("card-title");
        if (task.getStatus() == Status.DONE) titleLbl.getStyleClass().add("card-title-done");

        HBox metaRow = new HBox(10);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        if (task.getDueDate() != null) {
            Label dateLbl = new Label("📅 " + task.getDueDate());
            dateLbl.getStyleClass().add("card-meta");
            metaRow.getChildren().add(dateLbl);
        }

        if (task.getReminderTime() != null) {
            Label remLbl = new Label("⏰ " + task.getReminderTime());
            remLbl.getStyleClass().add("card-meta");
            metaRow.getChildren().add(remLbl);
        }

        Label catLbl = new Label("🏷 " + task.getCategory());
        catLbl.getStyleClass().add("card-meta");
        metaRow.getChildren().add(catLbl);

        info.getChildren().addAll(titleLbl, metaRow);

        if (!task.getDescription().isEmpty()) {
            Label descLbl = new Label(task.getDescription());
            descLbl.getStyleClass().add("card-desc");
            descLbl.setWrapText(true);
            info.getChildren().add(descLbl);
        }

        // Priority badge
        Label priorityBadge = new Label(task.getPriority().name());
        priorityBadge.getStyleClass().addAll("priority-badge",
                "priority-" + task.getPriority().name().toLowerCase());

        // Status combo
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("PENDING", "IN_PROGRESS", "DONE");
        statusCombo.setValue(task.getStatus().name());
        statusCombo.getStyleClass().add("status-combo");
        statusCombo.setOnAction(e -> {
            task.setStatus(Status.valueOf(statusCombo.getValue()));
            checkBox.setSelected(task.getStatus() == Status.DONE);
            if (task.getStatus() == Status.DONE) {
                card.getStyleClass().add("task-card-done");
                titleLbl.getStyleClass().add("card-title-done");
            } else {
                card.getStyleClass().remove("task-card-done");
                titleLbl.getStyleClass().remove("card-title-done");
            }
        });

        // Delete button
        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + task.getTitle() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Delete");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    TaskStore.getInstance().removeTask(task);
                    if (card.getParent() instanceof Pane parent) {
                        parent.getChildren().remove(card);
                    }
                }
            });
        });

        card.getChildren().addAll(checkBox, info, priorityBadge, statusCombo, deleteBtn);
        return card;
    }
}
