package views;

import data.TaskStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import models.Task;
import models.Task.Status;

public class TaskCard {

    private static final String CARD_BG = "#1f2937";
    private static final String CARD_HOVER = "#263041";
    private static final String BORDER  = "#374151";

    public static HBox build(Task task, boolean isOverdue) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);

        String cardBg     = isOverdue ? "#2d1515"
                          : task.getStatus() == Status.DONE ? "#111f18"
                          : CARD_BG;
        String cardBorder = isOverdue ? "#ef444455"
                          : task.getStatus() == Status.DONE ? "#00d4aa33"
                          : BORDER;

        card.setStyle(
            "-fx-background-color:" + cardBg + ";" +
            "-fx-background-radius:14;" +
            "-fx-border-color:" + cardBorder + ";" +
            "-fx-border-radius:14; -fx-border-width:1;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.4),12,0,0,4);" +
            "-fx-padding: 16 18 16 14;"
        );

        // Coloured left accent bar
        String barColor = switch (task.getPriority()) {
            case HIGH   -> "#ef4444";
            case MEDIUM -> "#f59e0b";
            case LOW    -> "#00d4aa";
        };
        Rectangle bar = new Rectangle(4, 48);
        bar.setArcWidth(4); bar.setArcHeight(4);
        bar.setFill(Color.web(barColor));

        // Checkbox
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(task.getStatus() == Status.DONE);
        checkBox.getStyleClass().add("task-checkbox");
        checkBox.setOnAction(e -> {
            task.setStatus(checkBox.isSelected() ? Status.DONE : Status.PENDING);
            TaskStore.getInstance().updateTaskStatus(task);
            String bg2 = task.getStatus() == Status.DONE ? "#111f18" : CARD_BG;
            String bd2 = task.getStatus() == Status.DONE ? "#00d4aa33" : BORDER;
            card.setStyle(card.getStyle()
                .replaceAll("-fx-background-color:[^;]+;", "-fx-background-color:" + bg2 + ";")
                .replaceAll("-fx-border-color:[^;]+;", "-fx-border-color:" + bd2 + ";")
            );
        });

        // Info
        VBox info = new VBox(5);
        info.setStyle("-fx-background-color:transparent;");
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLbl = new Label(task.getTitle());
        titleLbl.setStyle("-fx-font-size:14.5px; -fx-font-weight:bold; -fx-text-fill:" +
                (task.getStatus() == Status.DONE ? "#4b5563; -fx-strikethrough:true;" : "#f3f4f6;"));

        HBox metaRow = new HBox(14);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        metaRow.setStyle("-fx-background-color:transparent;");
        if (task.getDueDate()     != null) metaRow.getChildren().add(meta("📅 " + task.getDueDate()));
        if (task.getReminderTime()!= null) metaRow.getChildren().add(meta("⏰ " + task.getReminderTime()));
        metaRow.getChildren().add(meta("🏷 " + task.getCategory()));
        info.getChildren().addAll(titleLbl, metaRow);

        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            Label desc = new Label(task.getDescription());
            desc.setStyle("-fx-font-size:12px; -fx-text-fill:#6b7280;");
            desc.setWrapText(true);
            info.getChildren().add(desc);
        }

        // Right controls
        VBox right = new VBox(8);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setStyle("-fx-background-color:transparent;");

        Label badge = new Label(task.getPriority().name());
        badge.getStyleClass().addAll("priority-badge", "priority-" + task.getPriority().name().toLowerCase());

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("PENDING", "IN_PROGRESS", "DONE");
        statusCombo.setValue(task.getStatus().name());
        statusCombo.getStyleClass().add("status-combo");
        statusCombo.setOnAction(e -> {
            task.setStatus(Status.valueOf(statusCombo.getValue()));
            TaskStore.getInstance().updateTaskStatus(task);
            checkBox.setSelected(task.getStatus() == Status.DONE);
            boolean done = task.getStatus() == Status.DONE;
            titleLbl.setStyle("-fx-font-size:14.5px; -fx-font-weight:bold; -fx-text-fill:" +
                    (done ? "#4b5563; -fx-strikethrough:true;" : "#f3f4f6;"));
        });

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setStyle("-fx-background-color:transparent;");

        Button editBtn = new Button("✏  Edit");
        editBtn.getStyleClass().add("btn-edit");
        editBtn.setOnAction(e -> EditTaskDialog.show(task, () -> {
            titleLbl.setText(task.getTitle());
            metaRow.getChildren().clear();
            if (task.getDueDate()     != null) metaRow.getChildren().add(meta("📅 " + task.getDueDate()));
            if (task.getReminderTime()!= null) metaRow.getChildren().add(meta("⏰ " + task.getReminderTime()));
            metaRow.getChildren().add(meta("🏷 " + task.getCategory()));
            badge.setText(task.getPriority().name());
            badge.getStyleClass().removeIf(s -> s.startsWith("priority-"));
            badge.getStyleClass().add("priority-" + task.getPriority().name().toLowerCase());
            statusCombo.setValue(task.getStatus().name());
        }));

        Button deleteBtn = new Button("🗑  Delete");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + task.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Delete");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    TaskStore.getInstance().removeTask(task);
                    if (card.getParent() instanceof Pane p) p.getChildren().remove(card);
                }
            });
        });

        actions.getChildren().addAll(editBtn, deleteBtn);
        right.getChildren().addAll(badge, statusCombo, actions);
        card.getChildren().addAll(bar, checkBox, info, right);
        return card;
    }

    private static Label meta(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:11.5px; -fx-text-fill:#6b7280;");
        return l;
    }
}
